package com.proptiger.data.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    private ObjectType getObjectType(DomainObject objectStr, CriteriaBuilder cb, EntityManager em) {
        CriteriaQuery<ObjectType> otQ = cb.createQuery(ObjectType.class);
        Root<ObjectType> ot = otQ.from(ObjectType.class);
        otQ.select(ot).where(cb.equal(ot.get("type"), objectStr.getText()));
        TypedQuery<ObjectType> query = em.createQuery(otQ);
        ObjectType objType = query.getSingleResult();
        return objType;
    }

    private ImageType getImageType(ObjectType objType, String imageTypeStr, CriteriaBuilder cb, EntityManager em) {
        // Get ImageType
        CriteriaQuery<ImageType> itQ = cb.createQuery(ImageType.class);
        Root<ImageType> it = itQ.from(ImageType.class);
        itQ.select(it).where(
                cb.and(cb.equal(it.get("objectTypeId"), objType.getId()), cb.equal(it.get("type"), imageTypeStr)));
        ImageType imageType = em.createQuery(itQ).getSingleResult();
        // Return
        return imageType;
    }

    /**
     * @param image
     *            the image to set
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ImageProcessingException
     */
    public Image insertImage(DomainObject objectStr, String imageTypeStr, long objectId, File orignalImage,
            File watermarkImage) {
        try {
            EntityManager em = emf.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            String originalHash = ImageUtil.fileMd5Hash(orignalImage);
            String watermarkHash = ImageUtil.fileMd5Hash(watermarkImage);

            // Image
            ObjectType objType = getObjectType(objectStr, cb, em);
            ImageType imageType = getImageType(objType, imageTypeStr, cb, em);
            Image image = new Image();
            image.setImageTypeId(imageType.getId());
            image.setObjectId(objectId);

            String[] directories = { String.valueOf(objType.getId()), String.valueOf(objectId),
                    String.valueOf(imageType.getId()), "" };

            String path = StringUtils.join(directories, "/");
            image.setPath(path);

            // MetaData
            ImageUtil.populateImageMetaInfo(orignalImage, image);

            // DateTime
            image.setOriginalHash(originalHash);
            image.setWaterMarkHash(watermarkHash);
            image.setOriginalName(originalHash + "." + ImageUtil.getImageFormat(orignalImage));
            image.setWaterMarkName("");
            image.setActive(false);
            em.getTransaction().begin();
            em.persist(image);
            em.getTransaction().commit();
            image.generateWaterMarkName();
            return image;
        } catch (Exception e) {
            throw new RuntimeException("Could not insert image", e);
        }
    }

    public void markImageAsActive(Image image) {
        EntityManager em = emf.createEntityManager();
        image.setActive(true);
        em.getTransaction().begin();
        em.merge(image);
        em.getTransaction().commit();
    }
}
