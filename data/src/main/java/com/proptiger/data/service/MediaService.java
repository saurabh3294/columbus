package com.proptiger.data.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
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
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.ResourceNotFoundException;

/**
 * 
 * @author azi
 * 
 */

public abstract class MediaService {
    protected MediaType          mediaType     = MediaType.Document;

    @Value("${imageTempPath}")
    protected String             tempDirPath;

    protected File               tempDir;

    @Autowired
    protected MediaDao           mediaDao;

    @Autowired
    protected ObjectTypeDao      objectTypeDao;

    @Autowired
    protected MediaTypeDao       mediaTypeDao;

    @Autowired
    protected ObjectMediaTypeDao objectMediaTypeDao;

    protected final String       PATHSEPARATOR = "/";

    protected final String       DOT           = ".";

    @Autowired
    protected AmazonS3Util       amazonS3Util;

    @Autowired
    protected PropertyReader     propertyReader;

    @PostConstruct
    private void init() {
        tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        MediaUtil.endpoints = propertyReader.getRequiredProperty(PropertyKeys.ENDPOINTS).split(",");
    }

    public List<Media> getMedia(DomainObject domainObject, Integer objectId, String objectMediaType) {
        List<Media> result = new ArrayList<>();
        if (objectMediaType == null || objectMediaType.isEmpty()) {
            result = mediaDao.findByObjectIdAndObjectTypeAndMediaType(objectId, domainObject.toString(), mediaType);
        }
        else {
            result = mediaDao.findByObjectIdAndObjectTypeAndMediaTypeAndObjectMediaType(
                    objectId,
                    domainObject.toString(),
                    mediaType,
                    objectMediaType);
        }
        return result;
    }

    public Media createMedia(
            DomainObject domainObject,
            Integer objectId,
            MultipartFile file,
            String objectMediaType,
            Media media) {

        File originalFile = null;
        try {
            Media finalMedia = new Media();

            ExclusionAwareBeanUtilsBean utilsBean = new ExclusionAwareBeanUtilsBean();
            utilsBean.copyProperties(finalMedia, media);

            originalFile = File.createTempFile("originalMedia", ".tmp", tempDir);

            file.transferTo(originalFile);

            int objectMediaTypeId = getObjectMediaTypeId(domainObject, objectMediaType);

            finalMedia.setObjectId(objectId);
            finalMedia.setObjectMediaTypeId(objectMediaTypeId);
            MediaUtil.populateBasicMediaAttributes(originalFile, finalMedia);
            finalMedia.setActive(false);

            preventDuplicateMediaInsertion(finalMedia.getContentHash(), domainObject.toString());

            mediaDao.save(finalMedia);

            String url = computeMediaS3Url(finalMedia, file.getOriginalFilename());

            amazonS3Util.uploadFile(url, originalFile);

            finalMedia.setActive(true);
            finalMedia.setUrl(url);
            mediaDao.save(finalMedia);
            return mediaDao.findOne(finalMedia.getId());
        }
        catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new ProAPIException(e);
        }
        finally {
            deleteFileFromDisc(originalFile);
        }
    }

    protected void deleteFileFromDisc(File file) {
        if (file != null) {
            file.delete();
        }
    }

    protected Integer getObjectMediaTypeId(DomainObject object, String objectMediaType) {
        Integer mediaTypeId = mediaTypeDao.getMediaTypeIdFromMediaTypeName(mediaType);
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

    protected void preventDuplicateMediaInsertion(String contentHash, String objectType) {
        List<Media> mediaList = mediaDao.findByContentHashAndObjectType(contentHash, objectType);
        if (mediaList.size() > 0) {
            throw new ResourceAlreadyExistException("Media Already Exists");
        }
    }

    protected String computeMediaS3Url(Media media, String fileName) {
        int objectMediaTypeId = media.getObjectMediaTypeId();
        ObjectMediaType objectMediaType = objectMediaTypeDao.findOne(objectMediaTypeId);
        return objectMediaType.getMediaTypeId() + PATHSEPARATOR
                + objectMediaType.getObjectTypeId()
                + PATHSEPARATOR
                + media.getObjectId()
                + PATHSEPARATOR
                + objectMediaType.getId()
                + PATHSEPARATOR
                + media.getId()
                + DOT
                + FilenameUtils.getExtension(fileName);
    }

    public void deleteMedia(Integer id) {
        Media media = mediaDao.findOne(id);
        if (media == null || !media.isActive()) {
            throw new ResourceNotFoundException();
        }
        media.setActive(false);
        mediaDao.save(media);
    }

    public Media updateMedia(Media media, Integer id) {
        Media savedMedia = mediaDao.findOne(id);
        if (savedMedia == null) {
            throw new ResourceNotFoundException();
        }
        else {
            ExclusionAwareBeanUtilsBean utilsBean = new ExclusionAwareBeanUtilsBean();
            try {
                utilsBean.copyProperties(savedMedia, media);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new ProAPIException("Error Copying Media Object", e);
            }
            savedMedia.setUpdatedAt(new Date());
            return mediaDao.save(savedMedia);
        }
    }
}