package com.proptiger.data.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.MogrifyCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.io.Files;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.enums.ImageResolution;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.ImageUtil;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ResourceAlreadyExistException;

/**
 * @author yugal
 * 
 */
@Service
public class ImageService {
    private static final String HYPHON = "-";
    private static Logger       logger = LoggerFactory.getLogger(ImageService.class);
    private static File         tempDir;

    @Autowired
    private ImageDao            imageDao;

    @Autowired
    protected PropertyReader    propertyReader;

    @Autowired
    private Caching             caching;

    private TaskExecutor        taskExecutor;

    @PostConstruct
    private void init() {
        ImageUtil.endpoints = propertyReader.getRequiredProperty(PropertyKeys.ENDPOINTS).split(",");
        ImageUtil.bucket = propertyReader.getRequiredProperty(PropertyKeys.BUCKET);

        String path = propertyReader.getRequiredProperty(PropertyKeys.IMAGE_TEMP_PATH);
        tempDir = new File(path);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        taskExecutor = new SimpleAsyncTaskExecutor();
    }

    private void applyWaterMark(File file, String format) throws IOException {
        URL url = this.getClass().getClassLoader().getResource("watermark.png");
        InputStream waterMarkIS = new FileInputStream(url.getFile());
        BufferedImage waterMark = ImageIO.read(waterMarkIS);

        BufferedImage image = ImageIO.read(file);

        IMOperation imOps = new IMOperation();
        imOps.size(image.getWidth(), image.getHeight());
        imOps.addImage(2);
        imOps.geometry(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 4, image.getHeight() / 4);
        imOps.addImage();
        CompositeCmd cmd = new CompositeCmd();

        File outputFile = File.createTempFile("outputImage", Image.DOT + format, tempDir);

        try {
            cmd.run(imOps, waterMark, image, outputFile.getAbsolutePath());
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
        waterMarkIS.close();
    }

    private void uploadToS3(Image image, File original, File waterMark, String format) throws IllegalArgumentException,
            IOException {
        AmazonS3 s3 = createS3Instance();
        s3.putObject(ImageUtil.bucket, image.getPath() + image.getOriginalName(), original);
        original.delete();
        s3.putObject(ImageUtil.bucket, image.getPath() + image.getWaterMarkName(), waterMark);
        createAndUploadMoreResolutions(image, waterMark, format, s3);
    }

    /**
     * Creating more resolution for image file and uploading that to S3
     * 
     * @param image
     * @param waterMark
     * @param format
     * @param s3
     */
    private void createAndUploadMoreResolutions(
            final Image image,
            final File waterMark,
            final String format,
            final AmazonS3 s3) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (ImageResolution imageResolution : ImageResolution.values()) {
                    File resizedFile;
                    try {
                        resizedFile = resize(waterMark, imageResolution, format);
                        s3.putObject(
                                ImageUtil.bucket,
                                image.getPath() + computeResizedImageName(image, imageResolution, format),
                                resizedFile);
                        resizedFile.delete();
                    }
                    catch (Exception e) {
                        logger.error("Could not resize image for resolution name {}", imageResolution.getLabel(), e);
                    }
                }
                waterMark.delete();
            }
        });
    }

    private File resize(File waterMark, ImageResolution imageResolution, String format) throws Exception {
        ConvertCmd convertCmd = new ConvertCmd();
        IMOperation imOperation = new IMOperation();
        imOperation.addImage(waterMark.getAbsolutePath());
        imOperation.resize(imageResolution.getWidth(), imageResolution.getHeight(), ">");
        imOperation.strip();
        imOperation.quality(95.0);
        imOperation.interlace("Plane");
        File outputFile = File.createTempFile("resizedImage", Image.DOT + format, tempDir);
        imOperation.addImage(outputFile.getAbsolutePath());
        convertCmd.run(imOperation);
        return outputFile;
    }

    private String computeResizedImageName(Image image, ImageResolution imageResolution, String format) {
        return image.getId() + HYPHON
                + imageResolution.getWidth()
                + HYPHON
                + imageResolution.getHeight()
                + Image.DOT
                + format;
    }

    private AmazonS3 createS3Instance() {
        String accessKeyId = propertyReader.getRequiredProperty(PropertyKeys.ACCESS_KEY_ID);
        String secretAccessKey = propertyReader.getRequiredProperty(PropertyKeys.SECRET_ACCESS_KEY);

        ClientConfiguration config = new ClientConfiguration();
        config.withProtocol(Protocol.HTTP);
        config.setMaxErrorRetry(3);

        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        AmazonS3 s3 = new AmazonS3Client(credentials, config);
        return s3;
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
            Image imageParams) {

        // WaterMark by default (true)
        addWaterMark = (addWaterMark != null) ? addWaterMark : true;
        try {
            // Upload file
            File originalFile = File.createTempFile("originalImage", ".tmp", tempDir);

            if (fileUpload.isEmpty())
                throw new IllegalArgumentException("Empty file uploaded");
            fileUpload.transferTo(originalFile);
            String format = ImageUtil.getImageFormat(originalFile);

            // Image uploaded
            File processedFile = File.createTempFile("processedImage", Image.DOT + format, tempDir);
            Files.copy(originalFile, processedFile);

            if (addWaterMark) {
                applyWaterMark(processedFile, format);
            }

            String originalHash = ImageUtil.fileMd5Hash(originalFile);

            Long imageId = isImageHashExists(originalHash, objectId, object.getText());
            if (imageId != null)
                throw new ResourceAlreadyExistException("Image Already Exists with Id-" + imageId);

            // Persist
            Image image = imageDao.insertImage(
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

            caching.deleteMultipleResponseFromCache(getImageCacheKey(object, imageTypeStr, objectId));
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

    public Image getImage(long id) {
        return imageDao.findOne(id);
    }

    public void deleteImageInCache(long id) {
        Image image = getImage(id);

        caching.deleteMultipleResponseFromCache(getImageCacheKeyFromImageObject(image));
    }

    public String[] getImageCacheKey(DomainObject object, String imageTypeStr, long objectId) {
        String keys[] = new String[2];
        keys[0] = object.getText() + imageTypeStr + objectId;
        keys[0] = object.getText() + "null" + objectId;

        return keys;
    }

    public void update(Image image) {
        caching.deleteMultipleResponseFromCache(getImageCacheKeyFromImageObject(image));
        imageDao.save(image);
    }

    private String[] getImageCacheKeyFromImageObject(Image image) {
        DomainObject domainObject = DomainObject.valueOf(image.getImageTypeObj().getObjectType().getType());

        return getImageCacheKey(domainObject, image.getImageTypeObj().getType(), image.getObjectId());
    }

    private Long isImageHashExists(String originalHash, long objectId, String objectType) {
        List<Long> imageIds = imageDao.getImageOnHashAndObjectIdAndObjectType(originalHash, objectId, objectType);

        if (imageIds == null || imageIds.isEmpty())
            return null;

        return imageIds.get(0);
    }
}
