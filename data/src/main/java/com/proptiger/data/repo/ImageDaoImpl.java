package com.proptiger.data.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.drew.imaging.ImageProcessingException;
import com.proptiger.data.model.ObjectType;
import com.proptiger.data.model.enums.DomainObject;
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

}