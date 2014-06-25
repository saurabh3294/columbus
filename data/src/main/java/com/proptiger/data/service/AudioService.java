package com.proptiger.data.service;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.MediaType;
import com.proptiger.data.model.AudioAttributes;
import com.proptiger.data.model.Media;
import com.proptiger.data.repo.AudioAttributeDao;
import com.proptiger.exception.ProAPIException;

@Service
public class AudioService extends MediaService {

    private Logger            logger = LoggerFactory.getLogger(AudioService.class);

    @Autowired
    private AudioAttributeDao audioAttributeDao;

    public AudioService() {
        mediaType = MediaType.Audio;
    }

    @Override
    public Media createMedia(DomainObject domainObject, Integer objectId, File file, String objectMediaType, Media media) {
        try {
            Media finalMedia = super.createMedia(domainObject, objectId, file, objectMediaType, media);
            int uniqueId = finalMedia.getId();
            AudioAttributes audioAttributes = extractAndPopulateAudioAttributes(uniqueId, file);
            audioAttributeDao.save(audioAttributes);
            return finalMedia;
        }
        catch (Exception ex) {
            logger.error("Exception while creating audio.", ex);
            throw new ProAPIException(ex);
        }
        finally {
            deleteFileFromDisc(file);
        }
    }

    @Override
    public void deleteMedia(Integer id) {
        // TODO Auto-generated method stub
        super.deleteMedia(id);
    }

    @Override
    public List<Media> getMedia(DomainObject domainObject, Integer objectId, String objectMediaType) {
        return super.getMedia(domainObject, objectId, objectMediaType);
    }

    @Override
    public Media updateMedia(Media media, Integer id) {
        return super.updateMedia(media, id);
    }

    private AudioAttributes extractAndPopulateAudioAttributes(int uniqueId, File file) {
        // TODO Auto-generated method stub
        return null;
    }

}
