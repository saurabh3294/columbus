package com.proptiger.data.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.drew.imaging.ImageProcessingException;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.ObjectType;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ObjectMediaType;
import com.proptiger.data.util.MediaUtil;

@Repository
public class ImageDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private ObjectMediaTypeDao   objectMediaTypeDao;

    @Autowired
    private ObjectTypeDao        objectTypeDao;

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

            String watermarkHash = MediaUtil.fileMd5Hash(watermarkImage);

            // Image
            ObjectType objType = objectTypeDao.findByType(objectStr.toString());
            ObjectMediaType objectMediaType = objectMediaTypeDao.findByMediaTypeIdAndObjectTypeIdAndType(
                    1,
                    objType.getId(),
                    imageTypeStr);
            image.setImageTypeId(objectMediaType.getId());
            image.setObjectId(objectId);

            String[] directories = {
                    String.valueOf(objType.getId()),
                    String.valueOf(objectId),
                    String.valueOf(objectMediaType.getId()),
                    "" };

            String path = StringUtils.join(directories, File.separator);
            image.setPath(path);

            // MetaData
            MediaUtil.populateImageMetaInfo(orignalImage, image);

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

    public List<Image> getImageOnHashAndObjectType(String originalHash, String objectType) {

        EntityManager em = emf.createEntityManager();
        Query query = em
                .createNativeQuery(
                        "SELECT * FROM Image as I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ObjectType O ON (IT.objecttype_id=O.id) " + " WHERE I.original_hash = '"
                                + originalHash
                                + "' AND O.type = '"
                                + objectType
                                + "' AND I.active = 1 UNION "
                                + " SELECT * FROM Image as I JOIN ImageType IT ON (I.imagetype_id=IT.id) JOIN ObjectType O ON (IT.objecttype_id=O.id) "
                                + " WHERE I.watermark_hash = '"
                                + originalHash
                                + "' AND O.type = '"
                                + objectType
                                + "' AND I.active = 1 ",
                        Image.class);
        List<Image> result = query.getResultList();
        return result;
    }
}