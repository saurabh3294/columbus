package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.model.cms.LandMark;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.ProjectDB;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.Bank;
import com.proptiger.core.model.proptiger.Image;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.UtilityClass;
import com.proptiger.data.repo.ImageDao;
import com.proptiger.data.util.MediaUtil;

@Service
public class ImageEnricher {

    @Autowired
    private ImageService  imageService;

    @Autowired
    private ImageDao      imageDao;
    
    @Autowired
    private LandMarkService            localityAmenityService;
    
    private static Integer MAX_LANDMARK_IMAGE_COUNT = 100;

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

        List<Long> localityIds = new ArrayList<Long>();
        for(Locality locality : localities) {
            localityIds.add(new Long(locality.getLocalityId()));
        }
        
        Map<Long, List<Image>> imagesMap = getImagesMap(DomainObject.locality, localityIds);
        if (imagesMap == null) {
            return;
        }
        
        for(Locality  locality : localities) {
            List<Image> images = imagesMap.get(new Long(locality.getLocalityId()));
            if (images != null && images.size() > 0) {
                locality.setImageCount(images.size());
                if (imageCount == null || imageCount < 0 || imageCount > images.size()) {
                    imageCount = images.size();
                }
                locality.setImages(new ArrayList<Image>(images.subList(0, imageCount)));
            }
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

        Image image = imageService.getImage(imageId);
        if (image != null) {
            // setting image sitemap enabled field 0 for random project main image
            image.getImageTypeObj().setImageSitemapEnabled(0);
        }
        return image;
    }

    public void setBuilderImages(Builder builder) {
        if (builder == null) {
            return;
        }
        List<Image> images = imageService.getImages(DomainObject.builder, null, builder.getId());
        builder.setImages(images);
    }

    public void setImagesOfProjects(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return;
        }
        List<Long> projectIds = new ArrayList<>();
        for (Project project : projects) {
            projectIds.add(new Long(project.getProjectId()));
        }
        Map<Long, List<Image>> imagesMap = getImagesMap(DomainObject.project, projectIds);
        if (imagesMap == null) {
            return;
        }
        for (Project project : projects) {
            project.setImages(imagesMap.get(new Long(project.getProjectId())));
        }
    }

    public void setImagesOfBuilders(List<Builder> builders) {
        if (builders == null || builders.isEmpty()) {
            return;
        }
        List<Long> builderIds = new ArrayList<>();
        for (Builder builder : builders) {
            builderIds.add(new Long(builder.getId()));
        }
        Map<Long, List<Image>> imagesMap = getImagesMap(DomainObject.builder, builderIds);
        if (imagesMap == null) {
            return;
        }
        for (Builder builder : builders) {
            builder.setImages(imagesMap.get(new Long(builder.getId())));
        }
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
    
    public void setLocalityAmenitiesImages(Locality locality) {
        if (locality == null) {
            return;
        }
        
        List<LandMark> amenities = localityAmenityService.getLocalityAmenities(locality.getLocalityId(), null);
        List<Long> amenityIds = localityAmenityService.getIdListFromAmenities(amenities);
        List<Image> images = imageService.getImages(DomainObject.landmark, null, amenityIds);
        if (images == null || images.isEmpty()) {
            return;
        }
        locality.setLandmarkImages(getEqualImagesDistributionAmongImageTypes(images));
    }
    
    private List<Image> getEqualImagesDistributionAmongImageTypes(List<Image> images) {
        if (images.size() < MAX_LANDMARK_IMAGE_COUNT) {
            return images;
        } 
        
        Map<String, List<Image>> imageTypeVsImageListMap = new LinkedHashMap<String, List<Image>>();
        for(Image image : images) {
            List<Image> imageList = imageTypeVsImageListMap.get(image.getImageTypeObj().getType());
            if (imageList == null ) {
                imageList = new ArrayList<Image>();
            }
            imageList.add(image);
            imageTypeVsImageListMap.put(image.getImageTypeObj().getType(), imageList);    
        }
        Set<String> imageTypeSet = imageTypeVsImageListMap.keySet();
        Integer imageCount = 0;
        Map<String, List<Image>> processedMap = new LinkedHashMap<String, List<Image>>();
        
        while (imageCount < MAX_LANDMARK_IMAGE_COUNT) {
            for(String imageType : imageTypeSet) {
                List<Image> imageList = imageTypeVsImageListMap.get(imageType);
                if (imageList != null && !imageList.isEmpty() && imageCount < MAX_LANDMARK_IMAGE_COUNT) {
                    Image img = imageList.remove(0);
                    List<Image> list = processedMap.get(imageType);
                    if (list == null) {
                        list = new ArrayList<Image>();
                    }
                    list.add(img);
                    processedMap.put(imageType, list);
                    imageTypeVsImageListMap.put(imageType, imageList);
                    imageCount++;
                }
            }
        }
        
        List<Image> processedImageList = new ArrayList<Image>();
        for(String imageType : imageTypeSet) {
            if (processedMap.get(imageType) != null) {
                processedImageList.addAll(processedMap.get(imageType));
            }
        }
        return processedImageList;
    }


    public void setProjectAmenitiesImages(Project project) {
        if (project == null) {
            return;
        }

        List<LandMark> amenities = project.getNeighborhood() != null
                ? project.getNeighborhood()
                : localityAmenityService.getLandMarksForProject(project, null, null);
        if (amenities == null || amenities.isEmpty()) {
            return;
        }
        List<Long> amenityIds = localityAmenityService.getIdListFromAmenities(amenities);
        List<Image> images = imageService.getImages(DomainObject.landmark, null, amenityIds);
        if (images == null) {
            return;
        }
        project.setLandmarkImages(images);
    }

    public PaginatedResponse<List<Image>> getCityAmenityImages(List<LandMark> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return new PaginatedResponse<List<Image>>();
        }
        List<Long> amenityIds = localityAmenityService.getIdListFromAmenities(amenities);
        List<Image> images = imageService.getImages(DomainObject.landmark, null, amenityIds);
        if (images == null || images.isEmpty()) {
            return new PaginatedResponse<List<Image>>();
        }
        List<Image> orderedImages = getEqualImagesDistributionAmongImageTypes(images);
        return new PaginatedResponse<List<Image>>(orderedImages, orderedImages.size());
    }

    public void setAmenitiesImages(List<LandMark> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return;
        }
        List<Long> amenityIds = localityAmenityService.getIdListFromAmenities(amenities);
        Map<Long, List<Image>> imagesMap = getImagesMap(DomainObject.landmark, amenityIds);
        for(LandMark amenity : amenities) {
            List<Image> images = imagesMap.get(new Long(amenity.getId()));
            if (images != null) {
                amenity.setImages(images);
            }
        }
    }
}
