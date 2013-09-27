package com.proptiger.data.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageProcessingException;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.repo.ImageDaoImpl;
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
	private ImageDaoImpl dao;

	private boolean isEmpty(MultipartFile file) {
		return (file.getSize() == 0) ? true : false;
	}

	private void convertToJPG(File image, File jpg) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(image);
		ImageIO.write(img, "jpg", jpg); // Writes at 0.7 compression quality
	}

	private void addWaterMark(File jpgFile) throws IOException {
		URL url = this.getClass().getClassLoader().getResource("watermark.png");
		InputStream waterMarkIS = new FileInputStream(url.getFile());
		BufferedImage waterMark = ImageIO.read(waterMarkIS);

		BufferedImage image = ImageIO.read(jpgFile);
		Graphics2D g = image.createGraphics();
		try {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% transparent
			g.drawImage(waterMark,
					(image.getWidth() - waterMark.getWidth()) / 2,
					(image.getHeight() - waterMark.getHeight()) / 2, null);
		} finally {
			g.dispose();
		}
		ImageIO.write(image, "jpg", jpgFile);
	}

	private void uploadToS3(File original, File watermark) {
	}

	/*
	 * Public method to get images
	 */
	public List<Image> getImages(DomainObject object, String imageTypeStr,
			int objId) {
		if (imageTypeStr == null) {
			return imageDao.getImagesForObject(object.getText(), objId);
		} else {
			return imageDao.getImagesForObjectWithImageType(object.getText(),
					imageTypeStr, objId);
		}
	}

	/*
	 * Public method to upload images
	 */
	public void uploadImage(DomainObject object, String imageTypeStr,
			int objId, MultipartFile fileUpload) {
		try {
			// Upload file
			File originalFile = File.createTempFile("originalImage", ".tmp", tempDir);
			if (isEmpty(fileUpload))
				throw new IllegalArgumentException("Empty file uploaded");
			fileUpload.transferTo(originalFile);
			if (!ImageUtil.isValidImage(originalFile)) {
				originalFile.delete();
				throw new IllegalArgumentException("Uploaded file is not an image");
			}
			// Image uploaded
			File jpgFile = File.createTempFile("jpgImage", ".jpeg", tempDir);
			convertToJPG(originalFile, jpgFile);
			addWaterMark(jpgFile);
			// Persist
			dao.setImage(object, imageTypeStr, objId, originalFile, jpgFile);
			dao.save();
			uploadToS3(originalFile, jpgFile);
			// Update Image
		} catch (IllegalStateException | IOException | ImageProcessingException e) {
			throw new RuntimeException("Something went wrong");
		}
	}
}
