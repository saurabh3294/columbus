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
import com.proptiger.data.model.ObjectType;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ObjectMediaType;
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

    private ObjectMediaType getImageType(ObjectType objType, String imageTypeStr, CriteriaBuilder cb, EntityManager em) {
        // Get ImageType
        CriteriaQuery<ObjectMediaType> itQ = cb.createQuery(ObjectMediaType.class);
        Root<ObjectMediaType> it = itQ.from(ObjectMediaType.class);
        itQ.select(it).where(
                cb.and(cb.equal(it.get("objectTypeId"), objType.getId()), cb.equal(it.get("type"), imageTypeStr)));
        ObjectMediaType imageType = em.createQuery(itQ).getSingleResult();
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
    public Image insertImage(
            DomainObject objectStr,
            String imageTypeStr,
            long objectId,
            File orignalImage,
            File watermarkImage,
            Image image,
            String format,
            String originalHash) {
        try {
            EntityManager em = emf.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            String watermarkHash = ImageUtil.fileMd5Hash(watermarkImage);

            // Image
            ObjectType objType = getObjectType(objectStr, cb, em);
            ObjectMediaType imageType = getImageType(objType, imageTypeStr, cb, em);
            image.setImageTypeId(imageType.getId());
            image.setObjectId(objectId);

            String[] directories = {
                    String.valueOf(objType.getId()),
                    String.valueOf(objectId),
                    String.valueOf(imageType.getId()),
                    "" };

            String path = StringUtils.join(directories, File.separator);
            image.setPath(path);

            // MetaData
            ImageUtil.populateImageMetaInfo(orignalImage, image);

            // DateTime
            image.setOriginalHash(originalHash);
            image.setWaterMarkHash(watermarkHash);
            image.assignOriginalName(format);
            image.setWaterMarkName("");
            image.setActive(false);
            em.getTransaction().begin();
            em.persist(image);
            em.getTransaction().commit();
            image.assignWatermarkName(format);
            return image;
        }
        catch (Exception e) {
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
