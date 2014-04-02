package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Bank;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.repo.ImageDao;

@Service
public class ImageEnricher {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDao     imageDao;

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

        if (images == null)
            return;
        /*
         * Ready For Posssession, occupied Projects construction images should
         * not be included.
         */
        if (project.getProjectStatus().equalsIgnoreCase(Project.ProjectStatus.Occupied.getStatus()) || project
                .getProjectStatus().equalsIgnoreCase(Project.ProjectStatus.ReadyForPossession.getStatus())) {
            Iterator<Image> it = images.iterator();
            while (it.hasNext()) {
                if (it.next().getImageTypeObj().getType().equalsIgnoreCase("constructionStatus")) {
                    it.remove();
                }
            }
        }
        else {
            int numImages = 0;
            Iterator<Image> it = images.iterator();
            while (it.hasNext()) {
                if (it.next().getImageTypeObj().getType().equalsIgnoreCase("constructionStatus")) {
                    numImages++;
                    if (numImages > 10) {
                        it.remove();
                    }
                }

            }
        }
        
        project.setImages(images);

    }

    @Deprecated
    public void setProjectDBImages(ProjectDB project) {
        if (project == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.project, null, project.getProjectId());

        if (images != null) {
            for (Image image : images) {
                if (image.getImageTypeObj().getType().equals("main")) {
                    project.setImageURL(image.getAbsolutePath());
                    break;
                }
            }

            /*
             * Ready For Posssession, occupied Projects construction images
             * should not be included.
             */
            if (project.getProjectStatus().equalsIgnoreCase(Project.ProjectStatus.Occupied.getStatus()) || project
                    .getProjectStatus().equalsIgnoreCase(Project.ProjectStatus.ReadyForPossession.getStatus())) {
                Iterator<Image> it = images.iterator();
                while (it.hasNext()) {
                    if (it.next().getImageTypeObj().getType().equalsIgnoreCase("constructionStatus")) {
                        it.remove();
                    }

                }
            }
        }

        project.setImages(images);

    }

    public void setPropertiesImages(List<Property> properties) {
        if (properties == null || properties.isEmpty())
            return;

        List<Long> propertyIds = new ArrayList<>();
        for (Property property : properties) {
            propertyIds.add(new Long(property.getPropertyId()));
        }
        List<Image> images = imageService.getImages(DomainObject.property, null, propertyIds);
        if (images == null)
            return;

        Map<Long, List<Image>> imagesMap = new HashMap<>();
        List<Image> domainImages;
        for (Image image : images) {

            domainImages = imagesMap.get(image.getObjectId());

            if (domainImages == null) {
                domainImages = new ArrayList<>();
                imagesMap.put(image.getObjectId(), domainImages);
            }

            domainImages.add(image);
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

    public void setLocalityImages(Locality locality, Integer numberOfImages) {
        if (locality == null)
            return;

        List<Image> images = imageService.getImages(DomainObject.locality, null, locality.getLocalityId());
        if (images != null && images.size() > 0) {
            locality.setImageCount(images.size());

            if (numberOfImages == null || numberOfImages < 0 || numberOfImages > images.size())
                numberOfImages = images.size();

            locality.setImages(images.subList(0, numberOfImages));
        }
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
                    bank.setImages(images.subList(0, imageCount));
                }
            }
        }

    }

}
