package com.proptiger.data.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.drew.imaging.ImageProcessingException;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.ObjectType;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ImageType;
import com.proptiger.data.util.ImageUtil;

@Repository
public class ImageDaoImpl {
	@Autowired
	private EntityManagerFactory emf;
	
	private EntityManager em;
	private CriteriaBuilder cb;
	
	@PostConstruct
	private void init() {
		em = emf.createEntityManager();
		cb = em.getCriteriaBuilder();
	}
	
	private Image image;
	
	private ObjectType getObjectType(DomainObject objectStr) {
		CriteriaQuery<ObjectType> otQ = cb.createQuery(ObjectType.class);
		Root<ObjectType> ot = otQ.from(ObjectType.class);
		otQ.select(ot).where(cb.equal(ot.get("type"), objectStr.getText()));
        TypedQuery<ObjectType> query = em.createQuery(otQ);
        ObjectType objType = query.getSingleResult();
        return objType;
	}
	
	public ImageType getImageType(ObjectType objType, String imageTypeStr) {
        // Get ImageType
        CriteriaQuery<ImageType> itQ = cb.createQuery(ImageType.class);
		Root<ImageType> it = itQ.from(ImageType.class);
		itQ.select(it).where( cb.and(cb.equal(it.get("objectTypeId"), objType.getId()), cb.equal(it.get("type"), imageTypeStr)) );
        ImageType imageType = em.createQuery(itQ).getSingleResult();
        // Return
        return imageType;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ImageProcessingException 
	 */
	public void setImage(DomainObject objectStr, String imageTypeStr, int objId, File orignalImage, File watermarkImage) throws FileNotFoundException, IOException, ImageProcessingException {
		String originalHash, watermarkHash;
		originalHash = ImageUtil.fileMd5Hash(orignalImage);
		watermarkHash = ImageUtil.fileMd5Hash(watermarkImage);
		// Image
		ObjectType objType = getObjectType(objectStr);
        ImageType imageType = getImageType(objType, imageTypeStr);
		Image img = new Image();
		img.setImageTypeId(imageType.getId());
		img.setObjectId(objId);
		String[] directories = {
				"", String.valueOf(objType.getId()),
				String.valueOf(objId),
				String.valueOf(imageType.getId()),
				""
				};
		String path = StringUtils.join(directories, "/");
		img.setPath(path);
		// MetaData
		HashMap<String, Object> info;
		ImageUtil.getImageInfo(orignalImage);
		info = ImageUtil.getImageInfo(orignalImage);
		// DateTime
		Date date = (Date) info.get("datetime");
		img.setTakenAt((date != null)? (Date)date: null);
		img.setSizeInBytes((long) info.get("size_in_bytes"));
		img.setWidth(Integer.parseInt((String)info.get("width")));
		img.setHeight(Integer.parseInt((String)info.get("height")));
		Double lat = (Double) info.get("latitude");
		Double lng = (Double) info.get("longitude");
		img.setLatitude((lat != null)? (double)lat: null);
		img.setLongitude((lng != null)? (double)lng: null);
		img.setOriginalHash(originalHash);
		img.setWaterMarkHash(watermarkHash);
		img.setOriginalName(originalHash);
		img.setWaterMarkName(objectStr.getText() + objId + imageTypeStr);
		image = img;
	}
	
	public void save() {
		em.getTransaction().begin();
		em.persist(image);
		em.getTransaction().commit();
	}
}
