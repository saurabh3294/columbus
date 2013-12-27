package com.proptiger.data.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.im4java.core.CompositeCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.MogrifyCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.Caching;
import com.proptiger.data.util.ImageUtil;
import com.proptiger.data.util.PropertyReader;

/**
 * @author yugal
 * 
 */
@Service
public class ImageService {
	private static File tempDir;

	@Autowired
	protected PropertyReader propertyReader;
	
	@Autowired
	private Caching caching;
	
	private String cacheName = "cache";

	@PostConstruct
	private void init() {
        ImageUtil.endpoint = propertyReader.getRequiredProperty("endpoint");
        ImageUtil.bucket = propertyReader.getRequiredProperty("bucket");

        String path = propertyReader.getRequiredProperty("imageTempPath");
        tempDir = new File(path);
        if (!tempDir.exists()) {
			tempDir.mkdir();
		}
	}

	@Resource
	private ImageDao imageDao;

	private boolean isEmpty(MultipartFile file) {
		return (file.getSize() == 0) ? true : false;
	}

	private void convertToJPG(File image, File jpg) throws IOException {
		InputStream imageIS = new FileInputStream(image);
		BufferedImage img = ImageIO.read(imageIS);
		ImageIO.write(img, "jpg", jpg); // Writes at 0.7 compression quality
		imageIS.close();
	}

	private void applyWaterMark(File file) throws IOException {
		URL url = this.getClass().getClassLoader().getResource("watermark.png");
		InputStream waterMarkIS = new FileInputStream(url.getFile());
		BufferedImage waterMark = ImageIO.read(waterMarkIS);

		BufferedImage image = ImageIO.read(file);
		
		IMOperation imOps = new IMOperation();
		imOps.size(image.getWidth(), image.getHeight());
		imOps.dissolve(50);
		imOps.addImage(2);
		imOps.geometry(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 4, image.getHeight() / 4);
        imOps.addImage();
        CompositeCmd cmd = new CompositeCmd();

        File outputFile = File.createTempFile("outputImage", ".jpg", tempDir);

		try {
            cmd.run(imOps, waterMark, image, outputFile.getAbsolutePath());
            imOps = new IMOperation();
            imOps.strip();
            imOps.quality(95.0);
            imOps.interlace("Plane");
            imOps.addImage();
            MogrifyCmd command = new MogrifyCmd();
            command.run(imOps, outputFile.getAbsolutePath());
        } catch (InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Could not watermark image", e);
        }

		Files.copy(outputFile, file);
		outputFile.delete();
		waterMarkIS.close();
	}

	private void uploadToS3(Image image, File original, File waterMark) {
		AmazonS3 s3 = createS3Instance();
		s3.putObject(ImageUtil.bucket, image.getPath() + image.getOriginalName(), original);
		image.assignWatermarkName();
		s3.putObject(ImageUtil.bucket, image.getPath() + image.getWaterMarkName(), waterMark);
	}

    private AmazonS3 createS3Instance() {
        String accessKeyId = propertyReader.getRequiredProperty("accessKeyId");
		String secretAccessKey = propertyReader.getRequiredProperty("secretAccessKey");

		ClientConfiguration config = new ClientConfiguration();
		config.withProtocol(Protocol.HTTP);
		config.setMaxErrorRetry(3);

		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AmazonS3 s3 = new AmazonS3Client(credentials, config);
        return s3;
    }

	private void cleanUp(File original, File waterMark) {
		original.delete();
		waterMark.delete();
	}

	/*
	 * Public method to get images
	 */
	@Cacheable(value="cache", key="#object.getText()+#imageTypeStr+#objectId")
	public List<Image> getImages(DomainObject object, String imageTypeStr,
			long objectId) {
		if (imageTypeStr == null) {
			return imageDao.getImagesForObject(object.getText(), objectId);
		} else {
			return imageDao.getImagesForObjectWithImageType(object.getText(),
					imageTypeStr, objectId);
		}
	}

	/*
	 * Public method to upload images
	 */
	@CacheEvict(value="cache", key="#object.getText()+#imageTypeStr+#objectId")
	public Image uploadImage(DomainObject object, String imageTypeStr,
			long objectId, MultipartFile fileUpload, Boolean addWaterMark,
			Map<String, String> extraInfo) {
		
		// WaterMark by default (true)
		addWaterMark = (addWaterMark != null) ? addWaterMark : true;
		try {
			// Upload file
			File originalFile = File.createTempFile("originalImage", ".tmp",
					tempDir);
			if (isEmpty(fileUpload))
				throw new IllegalArgumentException("Empty file uploaded");
			fileUpload.transferTo(originalFile);
			if (!ImageUtil.isValidImage(originalFile)) {
				originalFile.delete();
				throw new IllegalArgumentException(
						"Uploaded file is not an image");
			}
			// Image uploaded
			File processedFile = File.createTempFile("jpgImage", ".jpg", tempDir);
			convertToJPG(originalFile, processedFile);

			if (addWaterMark) {
				applyWaterMark(processedFile);
			}

			// Persist
			Image image = imageDao.insertImage(object, imageTypeStr, objectId,
					originalFile, processedFile, extraInfo);
			uploadToS3(image, originalFile, processedFile);
			cleanUp(originalFile, processedFile);
			imageDao.markImageAsActive(image);
			return image;
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException("Something went wrong", e);
		}
	}

    public void deleteImage(long id) {
    	deleteImage(id);
        imageDao.setActiveFalse(id);
    }

    public Image getImage(long id) {
        return imageDao.findOne(id);
    }

    public Image createNewImage(long imageId, MultipartFile imageFile) {
        Image image = getImage(imageId);
        File originalFile;
        try {
            originalFile = File.createTempFile("originalImage", ".jpg", tempDir);
            imageFile.transferTo(originalFile);
            applyWaterMark(originalFile);
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException("Could not watermark image", e);
        }

        AmazonS3 s3 = createS3Instance();
        s3.putObject(ImageUtil.bucket, image.getPath() + image.getId() + "_v1.jpg", originalFile);
        image.setWaterMarkName(image.getId() + "_v1.jpg");
        return image;
    }
public void deleteImageInCache(long id){
    	Image image = getImage(id);
    	DomainObject domainObject = DomainObject.valueOf( image.getImageType().getObjectType().getType() );
    	
    	String cacheKey = getImageCacheKey(domainObject, image.getImageType().getType(), image.getId());
    	
    	caching.deleteResponseFromCache(cacheKey);
    }
    
    public String getImageCacheKey(DomainObject object, String imageTypeStr,
			long objectId){
    	
    	return object.getText()+imageTypeStr+objectId;
    }
}
