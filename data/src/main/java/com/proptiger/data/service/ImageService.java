package com.proptiger.data.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.ObjectType;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ImageType;
import com.proptiger.data.repo.ImageDao;
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

	@Autowired
	EntityManagerFactory emf;
	
	@Resource
	private ImageDao imageDao;
	
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
	    		EntityManager em = emf.createEntityManager();
	    		CriteriaBuilder cb = em.getCriteriaBuilder();
	    		CriteriaQuery<ObjectType> otQ = cb.createQuery(ObjectType.class);
	    		// Get ObjectType
	    		Root<ObjectType> ot = otQ.from(ObjectType.class);
	    		otQ.select(ot).where(cb.equal(ot.get("type"), object.getText()));
                TypedQuery<ObjectType> query = em.createQuery(otQ);
                ObjectType objType = query.getSingleResult();
                // Get ImageType
                CriteriaQuery<ImageType> itQ = cb.createQuery(ImageType.class);
	    		Root<ImageType> it = itQ.from(ImageType.class);
	    		itQ.select(it).where( cb.and(cb.equal(it.get("objectTypeId"), objType.getId()), cb.equal(it.get("type"), type)) );
                ImageType imageType = em.createQuery(itQ).getSingleResult();
                // Create Image
	    		Image image = new Image();
	    		image.setImageTypeId(imageType.getId());
	    		image.setObjectId(objId);
	    		image.setPath(object.getText() + "/" + objId + "/" + type + "/");
//	    		image.setTakenAt("");
//	    		image.setSize("");
//	    		image.setWidth("");
//	    		image.setHeight("");
	    		// Calculate File Hash
	    		MessageDigest md = MessageDigest.getInstance("MD5");
	    		FileInputStream fis = new FileInputStream(jpgFile);
	    		byte[] dataBytes = new byte[1024];
	            int nread = 0; 
	            while ((nread = fis.read(dataBytes)) != -1) {
	              md.update(dataBytes, 0, nread);
	            };
	            byte[] mdbytes = md.digest();
	            StringBuffer sb = new StringBuffer();
	            for (int i = 0; i < mdbytes.length; i++) {
	              sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	            }
	            // End calculate File Hash
	    		image.setContentName(sb.toString());
	    		image.setSeoName(object.getText() + objId + type);
	    		
	    		em.getTransaction().begin();
	    		em.persist(image);
	    		em.getTransaction().commit();
		} catch (IllegalStateException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}
}
