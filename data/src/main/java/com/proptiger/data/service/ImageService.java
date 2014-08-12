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
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.ImageResolution;
import com.proptiger.data.enums.MediaType;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ImageQuality;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.MediaUtil;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ResourceAlreadyExistException;

/**
 * @author yugal
 * 
 * @author azi
 * 
 */
@Service
public class ImageService extends MediaService {
    private static final String      HYPHON = "-";
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

    private Striped<Lock>       locks;

    @PostConstruct
    private void init() {
        locks = Striped.lock(propertyReader.getRequiredPropertyAsType("image.lock.stripes.count", Integer.class));
    }

    public ImageService() {
        mediaType = MediaType.Image;
        taskExecutor = new SimpleAsyncTaskExecutor();
    }

    private void applyWaterMark(File file, String format) throws IOException, InfoException {
        URL url = this.getClass().getClassLoader().getResource("watermark.png");

        Info info = new Info(file.getAbsolutePath());

        IMOperation imOps = new IMOperation();
        imOps.size(info.getImageWidth(), info.getImageWidth());
        imOps.addImage(2);
        imOps.geometry(
                info.getImageWidth() / 2,
                info.getImageWidth() / 2,
                info.getImageWidth() / 4,
                info.getImageWidth() / 4);
        imOps.addImage();
        CompositeCmd cmd = new CompositeCmd();

        File outputFile = File.createTempFile("outputImage", Image.DOT + format, tempDir);

        try {
            cmd.run(imOps, url.getFile(), file.getAbsolutePath(), outputFile.getAbsolutePath());

            imOps = new IMOperation();
            imOps.strip();

            imOps.quality(95.0);

            imOps.interlace("Plane");
            imOps.addImage();

            MogrifyCmd command = new MogrifyCmd();
            command.run(imOps, outputFile.getAbsolutePath());
        }
        catch (InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Could not watermark image", e);
        }

        Files.copy(outputFile, file);
        outputFile.delete();
    }

    private void uploadToS3(Image image, File original, File waterMark, String format) throws IllegalArgumentException,
            IOException {
        amazonS3Util.uploadFile(image.getPath() + image.getOriginalName(), original);
        original.delete();
        amazonS3Util.uploadFile(image.getPath() + image.getWaterMarkName(), waterMark);
        createAndUploadMoreResolutions(image, waterMark, format);
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
                    Integer resolutionId = getResolutionId(imageResolution.getLabel());

                    ImageQuality qualityObject = null;

                    if (resolutionId != null)
                        qualityObject = getOptimalQuality((int) image.getImageTypeId(), resolutionId);

                    File resizedFile = null;
                    try {

                        resizedFile = resize(waterMark, imageResolution, Image.BEST_QUALITY, format);

                        // upload original as well
                        amazonS3Util.uploadFile(
                                image.getPath() + computeResizedImageName(image, imageResolution, null, format),
                                resizedFile);

                        // resize the image for optimal quality
                        if (qualityObject != null) {
                            resizedFile = resize(waterMark, imageResolution, qualityObject.getQuality(), format);
                            amazonS3Util.uploadFile(
                                    image.getPath() + computeResizedImageName(
                                            image,
                                            imageResolution,
                                            Image.OPTIMAL_SUFFIX,
                                            format),
                                    resizedFile);
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

    private File resize(File waterMark, ImageResolution imageResolution, double quality, String format)
            throws Exception {
        ConvertCmd convertCmd = new ConvertCmd();
        IMOperation imOperation = new IMOperation();
        ;
        imOperation.addImage(waterMark.getAbsolutePath());
        imOperation.resize(imageResolution.getWidth(), imageResolution.getHeight(), ">");
        imOperation.strip();
        imOperation.quality(quality);

        if (!imageResolution.getLabel().equalsIgnoreCase("thumbnail"))
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

            String originalHash = MediaUtil.fileMd5Hash(originalFile);
            Image image = null;
            Lock lock = locks.get(originalHash);
            try {
                lock.lock();
                Image duplicateImage = isImageHashExists(originalHash, object.getText());
                if (duplicateImage != null) {
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
