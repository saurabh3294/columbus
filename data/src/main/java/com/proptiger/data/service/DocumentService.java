package com.proptiger.data.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.enums.MediaType;
import com.proptiger.core.model.proptiger.Media;
import com.proptiger.exception.ProAPIException;

@Service
public class DocumentService extends MediaService {
    
    private Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public DocumentService() {
        mediaType = MediaType.Document;
    }

    @Override
    public Media createMedia(DomainObject domainObject, Integer objectId, File file, String objectMediaType, Media media) {
        try {
            Media finalMedia = super.createMedia(domainObject, objectId, file, objectMediaType, media);
            return finalMedia;
        }
        catch (Exception ex) {
            logger.error("Exception while creating document.", ex);
            throw new ProAPIException(ex);
        }
        finally {
            deleteFileFromDisc(file);
        }
    }
}