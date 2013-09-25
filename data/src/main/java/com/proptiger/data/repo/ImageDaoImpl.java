package com.proptiger.data.repo;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	
	public ImageType getImageType(DomainObject object, String type) {
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
	 */
	public void setImage(DomainObject object, String imageTypeStr, int objId, File imageFile) {
        ImageType imageType = getImageType(object, imageTypeStr);
        // Create Image
		Image img = new Image();
		img.setImageTypeId(imageType.getId());
		img.setObjectId(objId);
		img.setPath(object.getText() + "/" + objId + "/" + imageTypeStr + "/");
//		image.setTakenAt("");
//		image.setSize("");
//		image.setWidth("");
//		image.setHeight("");
		try {
			img.setContentName(ImageUtil.fileMd5Hash(imageFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img.setSeoName(object.getText() + objId + imageTypeStr);
		image = img;
	}
	
	public void save() {
		em.getTransaction().begin();
		em.persist(image);
		em.getTransaction().commit();
	}
}
