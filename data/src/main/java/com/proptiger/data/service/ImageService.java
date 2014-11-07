package com.proptiger.data.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.core.MogrifyCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;
import com.google.common.util.concurrent.Striped;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.enums.MediaType;
import com.proptiger.core.exception.ResourceAlreadyExistException;
import com.proptiger.core.model.proptiger.Image;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.enums.ImageResolution;
import com.proptiger.data.model.image.ImageQuality;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.MediaUtil;

/**
 * @author yugal
 * 
 * @author azi
 * 
 */
@Service
public class ImageService extends MediaService {
    private static final String      HYPHON = "-";
    
    private static final String		 ORIGINAL = "ORIGINAL";
    
    private static Logger            logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageDao                 imageDao;

    @Autowired
    protected PropertyReader         propertyReader;

    @Autowired
    protected ImageResolutionService imageResolutionService;

    @Autowired
    protected ImageQualityService    imageQualityService;

    @Autowired
    private Caching                  caching;

    private TaskExecutor             taskExecutor;

    private Striped<Lock>            locks;

    @PostConstruct
    private void init() {
        locks = Striped.lock(PropertyReader.getRequiredPropertyAsType("image.lock.stripes.count", Integer.class));
    }

    public ImageService() {
        mediaType = MediaType.Image;
        taskExecutor = new SimpleAsyncTaskExecutor();
    }

    private void applyWaterMark(File file, String format) throws IOException, InfoException {
        URL url = this.getClass().getClassLoader().getResource("watermark.png");

        Info info = new Info(file.getAbsolutePath());

        IMOperation imOps = new IMOperation();
        imOps.size(info.getImageWidth(), info.getImageHeight());
        imOps.addImage(2);
        imOps.geometry(
                info.getImageWidth() / 2,
                info.getImageHeight() / 2,
                info.getImageWidth() / 4,
                info.getImageHeight() / 4);
        imOps.addImage();
        CompositeCmd cmd = new CompositeCmd();

        File outputFile = File.createTempFile("outputImage", Image.DOT + format, tempDir);

        try {
            cmd.run(imOps, url.getFile(), file.getAbsolutePath(), outputFile.getAbsolutePath());
        }
        catch (InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Could not watermark image", e);
        }
        
        Files.copy(outputFile, file);
        outputFile.delete();
    }

    private void applyQualityOptimization(File file) throws IOException {
        try {
            IMOperation imOps = new IMOperation();
            imOps.strip();

            imOps.quality(95.0);

            imOps.interlace("Plane");
            imOps.addImage();

            MogrifyCmd command = new MogrifyCmd();
            command.run(imOps, file.getAbsolutePath());
        }
        catch (InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Could not apply quality change", e);
        }
    }

    private void uploadToS3(Image image, File original, File waterMark, String format) throws IllegalArgumentException,
            IOException {
        amazonS3Util.uploadFile(image.getPath() + image.getOriginalName(), original);
        original.delete();
        amazonS3Util.uploadFile(image.getPath() + image.getWaterMarkName(), waterMark);
        uploadImageWithOptimalSuffix(image, waterMark, format);
        createAndUploadMoreResolutions(image, waterMark, format);
    }

	private void uploadImageWithOptimalSuffix(Image image, File waterMark,
			String format) {
		try {
			Integer resolutionId = getResolutionId(ORIGINAL);
			ImageQuality quality = getOptimalQuality((int) image.getImageTypeId(),
					resolutionId);
			File processedImage = null;
			if (quality != null) {
				processedImage = resizeAndQualityChange(waterMark, null, quality.getQuality(), format, true);
				amazonS3Util.uploadFile(image.getPath() + image.getId() + HYPHON + Image.OPTIMAL_SUFFIX + Image.DOT + format, processedImage);
			}
		}
		catch (Exception e) {
			logger.error("Could not optimize image quality for ", ORIGINAL, e);
		}
	}

	/**
     * Creating more resolution for image file and uploading that to S3
     * 
     * @param image
     * @param waterMark
     * @param format
     * @param s3
     */
    private void createAndUploadMoreResolutions(final Image image, final File waterMark, final String format) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                // not for all resolutions only
                for (ImageResolution imageResolution : ImageResolution.values()) {
                	if (imageResolution.getLabel().equals(ORIGINAL)) {
                		continue;
                	}
                	
                    Integer resolutionId = getResolutionId(imageResolution.getLabel());

                    ImageQuality qualityObject = null;

                    if (resolutionId != null)
                        qualityObject = getOptimalQuality((int) image.getImageTypeId(), resolutionId);

                    File resizedFile = null;
                    try {

                        resizedFile = resizeAndQualityChange(waterMark, imageResolution, Image.BEST_QUALITY, format, false);

                        // upload original as well
                        amazonS3Util.uploadFile(
                                image.getPath() + computeResizedImageName(image, imageResolution, null, format),
                                resizedFile);
                        deleteFileFromDisc(resizedFile);

                        // resize the image for optimal quality
                        if (qualityObject != null) {
                            resizedFile = resizeAndQualityChange(waterMark, imageResolution, qualityObject.getQuality(), format, false);
                            amazonS3Util.uploadFile(
                                    image.getPath() + computeResizedImageName(
                                            image,
                                            imageResolution,
                                            Image.OPTIMAL_SUFFIX,
                                            format),
                                    resizedFile);
                            deleteFileFromDisc(resizedFile);
                        }

                    }
                    catch (Exception e) {
                        logger.error("Could not resize image for resolution name {}", imageResolution.getLabel(), e);
                    }
                    finally {
                        deleteFileFromDisc(resizedFile);
                    }
                }
                deleteFileFromDisc(waterMark);
            }

        });
    }

    protected ImageQuality getOptimalQuality(int imageTypeId, int resolutionId) {
        return imageQualityService.getQuality(imageTypeId, resolutionId);
    }

    private Integer getResolutionId(String label) {
        return imageResolutionService.getResolutionId(label);

    }

    private File resizeAndQualityChange(File waterMark, ImageResolution imageResolution, double quality, String format, boolean isOriginalImage)
            throws Exception {
        ConvertCmd convertCmd = new ConvertCmd();
        IMOperation imOperation = new IMOperation();
        imOperation.addImage(waterMark.getAbsolutePath());
        if (!isOriginalImage) {
        	imOperation.resize(imageResolution.getWidth(), imageResolution.getHeight(), ">");
        }
        imOperation.strip();
        imOperation.quality(quality);
        
        //Thumbnail images are small in size, so they are marked as non-progressive.
        if (imageResolution != null && !imageResolution.getLabel().equalsIgnoreCase("thumbnail"))
            imOperation.interlace("Plane");

        File outputFile = File.createTempFile("resizedImage", Image.DOT + format, tempDir);
        imOperation.addImage(outputFile.getAbsolutePath());
        convertCmd.run(imOperation);
        return outputFile;
    }

    private String computeResizedImageName(
            Image image,
            ImageResolution imageResolution,
            String optimalSuffix,
            String format) {
        String resizedImageName;
        if (optimalSuffix != null) {

            resizedImageName = image.getId() + HYPHON
                    + imageResolution.getWidth()
                    + HYPHON
                    + imageResolution.getHeight()
                    + HYPHON
                    + optimalSuffix
                    + Image.DOT
                    + format;
        }
        else {

            resizedImageName = image.getId() + HYPHON
                    + imageResolution.getWidth()
                    + HYPHON
                    + imageResolution.getHeight()
                    + Image.DOT
                    + format;
        }
        return resizedImageName;
    }

    /*
     * Public method to get images
     */
    @Cacheable(value = Constants.CacheName.CACHE, key = "#object.getText()+#imageTypeStr+#objectId")
    public List<Image> getImages(DomainObject object, String imageTypeStr, long objectId) {
        logger.debug("Get images for domain object {} image type {} and id {}", object, imageTypeStr, objectId);
        if (imageTypeStr == null) {
            return imageDao.getImagesForObject(object.getText(), objectId);
        }
        else {
            return imageDao.getImagesForObjectWithImageType(object.getText(), imageTypeStr, objectId);
        }
    }

    /*
     * Public method to get images of multiple object ids
     */
    // Do not remove commented cacheable.
    // @Cacheable(value = Constants.CacheName.CACHE)
    public List<Image> getImages(DomainObject object, String imageTypeStr, List<Long> objectIds) {
        if (objectIds == null || objectIds.isEmpty())
            return new ArrayList<Image>();

        if (imageTypeStr == null) {
            return imageDao.getImagesForObjectIds(object.getText(), objectIds);
        }
        return imageDao.getImagesForObjectIdsWithImageType(object.getText(), imageTypeStr, objectIds);
    }

    /*
     * Public method to upload images
     */
    public Image uploadImage(
            DomainObject object,
            String imageTypeStr,
            long objectId,
            MultipartFile fileUpload,
            Boolean addWaterMark,
            Image imageParams) throws Exception {

        // WaterMark by default (true)
        addWaterMark = (addWaterMark != null) ? addWaterMark : true;
        try {
            // Upload file
            File originalFile = File.createTempFile("originalImage", ".tmp", tempDir);

            if (fileUpload.isEmpty())
                throw new IllegalArgumentException("Empty file uploaded");
            fileUpload.transferTo(originalFile);
            String format = MediaUtil.getImageFormat(originalFile);

            // Image uploaded
            File processedFile = File.createTempFile("processedImage", Image.DOT + format, tempDir);
            Files.copy(originalFile, processedFile);

            // Converting the image to RGB format.
            String colorspace = getColourSpace(processedFile);
            if (!colorspace.equalsIgnoreCase(Image.ColorSpace.sRGB.name()) && colorspace
                    .equalsIgnoreCase(Image.ColorSpace.RGB.name())) {

                File rgbFile = convertToRGB(processedFile, format);
                Files.copy(rgbFile, processedFile);
            }
            
            if (addWaterMark) {
                applyWaterMark(processedFile, format);
            }
            
            applyQualityOptimization(processedFile);
            
            String originalHash = MediaUtil.fileMd5Hash(originalFile);
            Image image = null;
            Lock lock = locks.get(originalHash);
            try {
                lock.lock();
                Image duplicateImage = isImageHashExists(originalHash, object.getText());
                if (duplicateImage != null) {
                    //If already existing images present then deleting originalFile and processedFile images.
                    deleteFileFromDisc(processedFile);
                    deleteFileFromDisc(originalFile);
                    throw new ResourceAlreadyExistException("This Image Already Exists for " + object.getText()
                            + " id-"
                            + duplicateImage.getObjectId()
                            + " with image id-"
                            + duplicateImage.getId()
                            + " under the category of "
                            + duplicateImage.getImageTypeObj().getType()
                            + ". The Image URL is: "
                            + duplicateImage.getAbsolutePath());
                }
                // Persist
                image = imageDao.insertImage(
                        object,
                        imageTypeStr,
                        objectId,
                        originalFile,
                        processedFile,
                        imageParams,
                        format,
                        originalHash);

                uploadToS3(image, originalFile, processedFile, format);
                imageDao.markImageAsActive(image);
            }
            finally {
                lock.unlock();
            }
            caching.deleteMultipleResponseFromCache(getImageCacheKey(object, imageTypeStr, objectId, image.getId()));
            return image;
        }
        catch (IllegalStateException | IOException e) {
            throw new RuntimeException("Could not process image", e);
        }
    }

    public void deleteImage(long id) {
        deleteImageInCache(id);
        imageDao.setActiveFalse(id);
    }

    @Cacheable(value = Constants.CacheName.CACHE, key = "'imageId:'+#id")
    public Image getImage(long id) {
        return imageDao.findOne(id);
    }

    public void deleteImageInCache(long id) {
        Image image = getImage(id);

        caching.deleteMultipleResponseFromCache(getImageCacheKeyFromImageObject(image));
    }

    public String[] getImageCacheKey(DomainObject object, String imageTypeStr, long objectId, long imageId) {
        String keys[] = new String[3];
        keys[0] = object.getText() + imageTypeStr + objectId;
        keys[1] = object.getText() + "null" + objectId;
        keys[2] = "imageId:" + imageId;

        return keys;

    }

    public void update(Image image) {
        caching.deleteMultipleResponseFromCache(getImageCacheKeyFromImageObject(image));
        imageDao.save(image);
    }

    private String[] getImageCacheKeyFromImageObject(Image image) {
        DomainObject domainObject = DomainObject.valueOf(image.getImageTypeObj().getObjectType().getType());

        return getImageCacheKey(domainObject, image.getImageTypeObj().getType(), image.getObjectId(), image.getId());
    }

    private Image isImageHashExists(String originalHash, String objectType) {
        List<Image> imageIds = imageDao.getImageOnHashAndObjectType(originalHash, objectType);

        if (imageIds == null || imageIds.isEmpty())
            return null;

        return imageIds.get(0);
    }

    private File convertToRGB(File imageFile, String format) throws Exception {
        ConvertCmd convertCmd = new ConvertCmd();
        IMOperation imOperation = new IMOperation();
        imOperation.addImage(imageFile.getAbsolutePath());
        imOperation.colorspace("sRGB");
        File outputFile = File.createTempFile("rgbImage", Image.DOT + format, tempDir);
        imOperation.addImage(outputFile.getAbsolutePath());
        convertCmd.run(imOperation);

        return outputFile;
    }

    private String getColourSpace(File imageFile) throws InfoException {

        Info info = new Info(imageFile.getAbsolutePath());
        return info.getProperty("Colorspace");
    }

}
