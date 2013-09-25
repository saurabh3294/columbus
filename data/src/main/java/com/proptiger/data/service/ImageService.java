package com.proptiger.data.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ImageType;
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
    	
	@Resource
	private ImageDao imageDao;

	@Autowired
	private ImageDaoImpl dao;

	@PostConstruct
	private void init() {
		String path = propertyReader.getRequiredProperty("imageTempPath");
		tempDir = new File(path);
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
	}
	
	private boolean isValidImage(MultipartFile file) {
		return (file.getSize() == 0)? false:true;
	}
	
	private File convertToJPG(File image) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(image);
		File jpg = File.createTempFile("imageJPG", ".jpg", tempDir);
		ImageIO.write(img, "jpg", jpg); // Writes at 0.7 compression quality
		return jpg;
	}

	private void makeProgresiveJPG(File jpgFile) {
	}
	
	private File createWatermarkedCopy(File jpgFile) throws IOException {
		InputStream waterMarkFile = ImageService.class.getResourceAsStream("/com/proptiger/data/service/watermark.png");
		BufferedImage waterMark = ImageIO.read(waterMarkFile);

		BufferedImage image = ImageIO.read(jpgFile);
		Graphics2D g = image.createGraphics();
		try {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% transparent
			g.drawImage(waterMark, (image.getWidth() - waterMark.getWidth())/2, (image.getHeight() - waterMark.getHeight())/2, null);
		}
		finally {
			g.dispose();
		}
		File waterMarkImageFile = File.createTempFile("imageWatermark", ".jpg", tempDir);
		ImageIO.write(image, "jpg", waterMarkImageFile);
		return waterMarkImageFile;
	}
	
	private void uploadToS3() {
	}
	
	private HashMap<String, String> getFileAttributes() {
		return new HashMap<String, String>();
	}
	
	/*
	 * Public method to get images
	 * */
	public List<Image> getImages(DomainObject object, String type, int objId) {
		if(type == null) {
			return imageDao.getImagesForObject(object.getText(), objId);			
		} else {
			return imageDao.getImagesForObjectWithImageType(object.getText(), type, objId);	
		}
	}
	
	/*
	 * Public method to upload images
	 * */
	public void uploadImage(DomainObject object, String type, int objId, MultipartFile imageFile) {
    	try {
	    		File tempFile = File.createTempFile("image", ".tmp", tempDir);
	    		File jpgFile, waterMarkImageFile;
	    		if(isValidImage(imageFile)) {
	    			imageFile.transferTo(tempFile);
	    			jpgFile = convertToJPG(tempFile);
	    			makeProgresiveJPG(jpgFile);
	    			waterMarkImageFile = createWatermarkedCopy(jpgFile);
	    		} else {
	    			throw new IllegalArgumentException();
	    		}
	    		// Upload to S3
                ImageType imageType = dao.getImageType(object, type);
                // Create Image
	    		Image image = new Image();
	    		image.setImageTypeId(imageType.getId());
	    		image.setObjectId(objId);
	    		image.setPath(object.getText() + "/" + objId + "/" + type + "/");
//	    		image.setTakenAt("");
//	    		image.setSize("");
//	    		image.setWidth("");
//	    		image.setHeight("");
	    		image.setContentName(ImageUtil.fileMd5Hash(jpgFile));
	    		image.setSeoName(object.getText() + objId + type);
	    		dao.save(image);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
}
