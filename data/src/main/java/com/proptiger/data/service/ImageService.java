package com.proptiger.data.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.repo.ImageDaoHelper;
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

	@PostConstruct
	private void init() {
		String path = propertyReader.getRequiredProperty("imageTempPath");
		tempDir = new File(path);
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
	}

	@Resource
	private ImageDao imageDao;

	@Autowired
	private ImageDaoHelper dao;

	private boolean isEmpty(MultipartFile file) {
		return (file.getSize() == 0) ? true : false;
	}

	private void convertToJPG(File image, File jpg) throws IOException {
		InputStream imageIS = new FileInputStream(image);
		BufferedImage img = ImageIO.read(imageIS);
		ImageIO.write(img, "jpg", jpg); // Writes at 0.7 compression quality
	}

	private BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_INDEXED,
				(IndexColorModel) image.getColorModel());
		Graphics2D g = resized.createGraphics();
		// Optimizations
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw
		g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(),
				image.getHeight(), null);
		g.dispose();
		return resized;
	}

	private void applyWaterMark(File jpgFile) throws IOException {
		URL url = this.getClass().getClassLoader().getResource("watermark.png");
		InputStream waterMarkIS = new FileInputStream(url.getFile());
		BufferedImage waterMark = ImageIO.read(waterMarkIS);

		BufferedImage image = ImageIO.read(jpgFile);
		Graphics2D g = image.createGraphics();

		// Resize watermark image
		int calWidth = (int) (0.5 * image.getWidth());
		int calHeight = (int) (0.5 * image.getHeight());
		BufferedImage resizedWaterMark = resize(waterMark, calWidth, calHeight);

		try {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.5f)); // 50% transparent
			g.drawImage(resizedWaterMark, (image.getWidth() - calWidth) / 2,
					(image.getHeight() - calHeight) / 2, null);
		} finally {
			g.dispose();
		}
		ImageIO.write(image, "jpg", jpgFile);
	}

	private void uploadToS3(Image image, File original, File waterMark) {
		String endpoint = propertyReader.getRequiredProperty("endpoint");
		String bucket = propertyReader.getRequiredProperty("bucket");
		String accessKeyId = propertyReader.getRequiredProperty("accessKeyId");
		String secretAccessKey = propertyReader
				.getRequiredProperty("secretAccessKey");

		ClientConfiguration config = new ClientConfiguration();
		config.withProtocol(Protocol.HTTP);
		config.setMaxErrorRetry(3);

		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId,
				secretAccessKey);
		AmazonS3 s3 = new AmazonS3Client(credentials, config);
		s3.putObject(bucket, image.getOriginalPath(), original);
		s3.putObject(bucket, image.getWaterMarkPath(), waterMark);
		image.setWaterMarkAbsolutePath(endpoint, bucket);
	}

	private void cleanUp(File original, File waterMark) {
		original.delete();
		waterMark.delete();
	}

	/*
	 * Public method to get images
	 */
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
			File jpgFile = File.createTempFile("jpgImage", ".jpeg", tempDir);
			convertToJPG(originalFile, jpgFile);
			if (addWaterMark) {
				applyWaterMark(jpgFile);
			}
			// Persist
			Image image = dao.insertImage(object, imageTypeStr, objectId,
					originalFile, jpgFile, extraInfo);
			uploadToS3(image, originalFile, jpgFile);
			cleanUp(originalFile, jpgFile);
			dao.markImageAsActive(image);
			return image;
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException("Something went wrong", e);
		}
	}
}
