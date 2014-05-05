package com.proptiger.data.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.Media;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.enums.MediaType;
import com.proptiger.data.model.image.ObjectMediaType;
import com.proptiger.data.repo.MediaDao;
import com.proptiger.data.repo.MediaTypeDao;
import com.proptiger.data.repo.ObjectMediaTypeDao;
import com.proptiger.data.repo.ObjectTypeDao;
import com.proptiger.data.util.AmazonS3Util;
import com.proptiger.data.util.MediaUtil;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceAlreadyExistException;

/**
 * 
 * @author azi
 * 
 */

public abstract class MediaService {
    protected MediaType        mediaType     = MediaType.Document;

    @Value("${imageTempPath}")
    private String             tempDirPath;

    private File               tempDir;

    @Autowired
    private MediaDao           mediaDao;

    @Autowired
    private ObjectTypeDao      objectTypeDao;

    @Autowired
    private MediaTypeDao       mediaTypeDao;

    @Autowired
    private ObjectMediaTypeDao objectMediaTypeDao;

    private String             pathSeparator = "/";

    @PostConstruct
    private void init() {
        tempDir = new File(tempDirPath);
    }

    public Media postMedia(
            DomainObject domainObject,
            Integer objectId,
            MultipartFile file,
            String objectMediaType,
            Media mediaParams) {
        Media media = new Media();

        try {
            File originalFile;
            originalFile = File.createTempFile("originalImage", ".tmp", tempDir);

            file.transferTo(originalFile);

            int objectMediaTypeId = getObjectMediaTypeId(domainObject, objectMediaType);

            media.setOriginalFileName(file.getOriginalFilename());
            media.setObjectId(objectId);
            media.setObjectMediaTypeId(objectMediaTypeId);
            MediaUtil.populateBasicMediaAttributes(originalFile, media);
            media.setIsActive(false);
            media.setPriority(0);

            preventDuplicateMediaInsertion(media.getContentHash(), 1);

            mediaDao.save(media);

            String url = getMediaS3Url(media);

            AmazonS3Util s3Util = new AmazonS3Util();
            s3Util.uploadFile(url, originalFile);

            media.setIsActive(true);
            media.setUrl(url);
            mediaDao.save(media);
            return media;
        }
        catch (IOException e) {
            throw new ProAPIException(e);
        }
    }

    protected Integer getObjectMediaTypeId(DomainObject object, String objectMediaType) {
        Integer mediaTypeId = mediaTypeDao.getMediaTypeIdFromMediaTypeName(mediaType.toString());
        Integer objectTypeId = objectTypeDao.getObjectTypeIdByType(object.toString());

        if (mediaTypeId == null || objectTypeId == null) {
            throw new BadRequestException();
        }
        else {
            ObjectMediaType type = objectMediaTypeDao.findByMediaTypeIdAndObjectTypeIdAndType(
                    mediaTypeId,
                    objectTypeId,
                    objectMediaType);
            if (type == null) {
                throw new BadRequestException();
            }
            else {
                return type.getId();
            }
        }
    }

    public void preventDuplicateMediaInsertion(String contentHash, Integer objectTypeId) {
        List<Media> mediaList = mediaDao.findByContentHashAndObjectTypeId(contentHash, objectTypeId);
        if (mediaList.size() > 0) {
            throw new ResourceAlreadyExistException("");
        }
    }

    protected String getMediaS3Url(Media media) {
        int objectMediaTypeId = media.getObjectMediaTypeId();
        ObjectMediaType objectMediaType = objectMediaTypeDao.findOne(objectMediaTypeId);
        return objectMediaType.getMediaTypeId() + pathSeparator
                + objectMediaType.getObjectTypeId()
                + pathSeparator
                + media.getObjectId()
                + pathSeparator
                + objectMediaType.getId()
                + pathSeparator
                + media.getId()
                + "."
                + FilenameUtils.getExtension(media.getOriginalFileName());
    }
}