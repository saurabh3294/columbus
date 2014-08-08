package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.MediaUtil;

@Service
public class ImageEnricher {

    @Autowired
    private ImageService  imageService;

    @Autowired
    private ImageDao      imageDao;

    private static Logger logger = LoggerFactory.getLogger(ImageEnricher.class);

    public void setProjectsImages(List<Project> projects) {
        if (projects == null || projects.isEmpty())
            return;

        for (Project project : projects) {
            setProjectImages(project);
        }
    }

    @Deprecated
    public void setProjectMainImage(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return;
        }

        List<Long> projectIds = new ArrayList<>();
        for (Project project : projects) {
            projectIds.add(new Long(project.getProjectId()));
        }

        List<Image> images = imageService.getImages(DomainObject.project, "main", projectIds);
        Map<Integer, String> imagesMap = new HashMap<>();
        if (images != null) {
            for (Image image : images) {
                imagesMap.put((int) image.getObjectId(), image.getAbsolutePath());
            }
        }

        for (Project project : projects) {
            if (imagesMap.containsKey(project.getProjectId())) {
                project.setImageURL(imagesMap.get(project.getProjectId()));
            }
        }
    }

    public void setProjectImages(Project project) {
        if (project == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.project, null, project.getProjectId());

        images = checkAndInsertProjectMainImageRandom(images, project.getImageURL());

        project.setImages(images);
        setProjectImagesByTypeCount(project, images);

    }

    @Deprecated
    public void setProjectDBImages(ProjectDB project) {
        if (project == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.project, null, project.getProjectId());

        images = checkAndInsertProjectMainImageRandom(images, project.getImageURL());

        project.setImages(images);
        setProjectDbImagesByTypeCount(project, images);

    }

    public void setPropertiesImages(List<Property> properties) {
        if (properties == null || properties.isEmpty())
            return;

        List<Long> propertyIds = new ArrayList<>();
        for (Property property : properties) {
            propertyIds.add(new Long(property.getPropertyId()));
        }
        Map<Long, List<Image>> imagesMap = getImagesMap(DomainObject.property, propertyIds);
        if (imagesMap == null) {
            return;
        }
        for (Property property : properties) {
            property.setImages(imagesMap.get(new Long(property.getPropertyId())));
        }
    }

    public void setPropertyImages(Property property) {
        if (property == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.property, null, property.getPropertyId());
        property.setImages(images);
        setProjectImages(property.getProject());

    }

    public void setLocalitiesImages(List<Locality> localities, Integer imageCount) {
        if (localities == null || localities.isEmpty())
            return;

        Locality locality;
        for (int i = 0; i < localities.size(); i++) {
            locality = localities.get(i);
            setLocalityImages(locality, imageCount);
        }
    }

    /**
     * Populate images of Locality, in case imagecount is null then populate all
     * images present in locality, caller need not to check null conditions
     * 
     * @param locality
     * @param numberOfImages
     */
    public void setLocalityImages(Locality locality, Integer numberOfImages) {
        if (locality == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.locality, null, locality.getLocalityId());
        if (images != null && images.size() > 0) {
            locality.setImageCount(images.size());

            if (numberOfImages == null || numberOfImages < 0 || numberOfImages > images.size()) {
                numberOfImages = images.size();
            }

            locality.setImages(new ArrayList<Image>(images.subList(0, numberOfImages)));
        }
    }

    /**
     * Returns the type of images and the count in a map for example {
     * layoutPlan=1, floorPlan=3 }
     * 
     * @param project
     * @param images
     */
    public void setProjectImagesByTypeCount(Project project, List<Image> images) {
        Map<String, Integer> imagesByTypeCount = new HashMap<String, Integer>();
        for (Image image : images) {
            String imageType = image.getImageTypeObj().getType();

            if (imagesByTypeCount.containsKey(imageType)) {
                imagesByTypeCount.put(imageType, imagesByTypeCount.get(imageType) + 1);

            }
            else {
                imagesByTypeCount.put(imageType, 1);
            }

        }
        project.setImageCountByType(imagesByTypeCount);

    }

    /**
     * Returns the type of images and the count in a map for example {
     * layoutPlan=1, floorPlan=3 }
     * 
     * @param project
     * @param images
     */
    public void setProjectDbImagesByTypeCount(ProjectDB project, List<Image> images) {
        Map<String, Integer> imagesByTypeCount = new HashMap<String, Integer>();
        for (Image image : images) {
            // Long imageTypeId = image.getImageTypeId();
            String imageType = image.getImageTypeObj().getType();

            if (imagesByTypeCount.containsKey(imageType)) {
                imagesByTypeCount.put(imageType, imagesByTypeCount.get(imageType) + 1);

            }
            else {
                imagesByTypeCount.put(imageType, 1);
            }

        }
        project.setImageCountByType(imagesByTypeCount);

    }

    /**
     * Set images of banks
     * 
     * @param banks
     * @param imageCount
     */
    public void setBankImages(List<Bank> banks, Integer imageCount) {
        if (banks != null && banks.size() > 0) {
            for (Bank bank : banks) {
                setBankImage(bank, imageCount);
            }
        }
    }

    /**
     * Set images in bank object
     * 
     * @param bank
     * @param imageCount
     */
    private void setBankImage(Bank bank, Integer imageCount) {
        if (bank != null) {
            List<Image> images = imageService.getImages(DomainObject.bank, null, bank.getId());
            if (images != null) {
                if (imageCount == null || imageCount > images.size()) {
                    bank.setImages(images);
                }
                else {
                    bank.setImages(new ArrayList<Image>(images.subList(0, imageCount)));
                }
            }
        }

    }

    /**
     * If the project does not contain any images or image list does not have
     * main image then random image is being inserted at the first index as It
     * is ensured that main image will come at top.
     * 
     * @param images
     * @param mainImageURL
     * @return
     */
    private List<Image> checkAndInsertProjectMainImageRandom(List<Image> images, String mainImageURL) {
        if (images == null) {
            images = new ArrayList<Image>();
        }

        if (images.isEmpty() || !images.get(0).getImageTypeObj().getType().equals("main")) {
            Image image = getProjectRandomMainImage(mainImageURL);
            if (image != null) {
                images.add(0, image);
            }
        }

        return images;
    }

    /**
     * Method returns default Project Image for Project's Main Url
     * 
     * @param mainImageUrl
     * @return
     */
    public Image getDefaultProjectImage(String projectMainImageUrl) {
        return getProjectRandomMainImage(projectMainImageUrl);
    }

    /**
     * In the case when the project does not contain the main image. A random
     * image is inserted into the project in the solr in the form of image path.
     * From the image path, the image id, watermark name and path is extracted
     * and inserted into the new image object. Sample Image URL: 1/4/6/111.jpeg
     * => 1/4/6/ is Path, 111.jpeg is watermark name and 111 is Image Id.
     * 
     * @param projectMainUrl
     * @return
     */
    private Image getProjectRandomMainImage(String projectMainUrl) {
        if (projectMainUrl == null || projectMainUrl.isEmpty())
            return null;

        int index1 = projectMainUrl.lastIndexOf('/');
        int index2 = projectMainUrl.lastIndexOf('.');
        long imageId = Long.parseLong(projectMainUrl.substring(index1 + 1, index2));

        String endpoint = MediaUtil.getMediaEndpoint(imageId);
        String path = projectMainUrl.substring(endpoint.length() + 1, index1 + 1);
        String waterMarkName = projectMainUrl.substring(index1 + 1);

        return imageService.getImage(imageId);
    }

    private Map<Long, List<Image>> getImagesMap(DomainObject domainObject, List<Long> objectIds) {
        Map<Long, List<Image>> imagesMap = new HashMap<>();
        List<Image> images = imageService.getImages(domainObject, null, objectIds);
        if (images == null) {
            return null;
        }
        List<Image> domainImages;
        for (Image image : images) {
            domainImages = imagesMap.get(image.getObjectId());
            if (domainImages == null) {
                domainImages = new ArrayList<>();
                imagesMap.put(image.getObjectId(), domainImages);
            }
            domainImages.add(image);
        }
        return imagesMap;
    }
}
