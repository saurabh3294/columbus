package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.model.Media;
import com.proptiger.data.model.Property;

@Service
public class MediaEnricher {
    @Autowired
    private DocumentService documentService;

    public void setPropertiesMedia(List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }

        List<Integer> propertyIds = new ArrayList<Integer>();
        for (Property property : properties) {
            propertyIds.add(new Integer(property.getPropertyId()));
        }

        Map<Integer, List<Media>> mediaMap = getMediaMap(DomainObject.property, propertyIds);
        if (mediaMap == null) {
            return;
        }
        for (Property property : properties) {
            property.setMedia(mediaMap.get(property.getPropertyId()));
        }
    }

    private Map<Integer, List<Media>> getMediaMap(DomainObject domainObject, List<Integer> propertyIds) {
        Map<Integer, List<Media>> mediaMap = new HashMap<Integer, List<Media>>();
        List<Media> mediaList = documentService.getMediaList(domainObject, null, propertyIds);
        if (mediaList == null) {
            return null;
        }

        List<Media> domainMedia = null;
        for (Media media : mediaList) {
            domainMedia = mediaMap.get(media.getObjectId());
            if (domainMedia == null) {
                domainMedia = new ArrayList<Media>();
                mediaMap.put(media.getObjectId(), domainMedia);
            }
            domainMedia.add(media);
        }
        return mediaMap;
    }
}
