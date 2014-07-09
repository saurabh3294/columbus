package com.proptiger.data.service;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
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
import com.proptiger.exception.ResourceAlreadyExistException;

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
            finalMedia.setAudioAttributes(audioAttributes);
            return finalMedia;
        }
        catch (ResourceAlreadyExistException rex) {
            throw rex;
        }
        catch (Exception ex) {
            logger.error("Exception while creating audio.", ex);
            throw new ProAPIException(ex);
        }
        finally {
            deleteFileFromDisc(file);
        }
    }

    /**
     * First fill default values then try to extract attributes.
     */
    private AudioAttributes extractAndPopulateAudioAttributes(int uniqueId, File file) {
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setId(uniqueId);
        try {
            AudioFile f = AudioFileIO.read(file);
            AudioHeader audioHeader = f.getAudioHeader();
            audioAttributes.setDuration(audioHeader.getTrackLength());
            audioAttributes.setSampleRate(audioHeader.getSampleRateAsNumber());
        }
        catch (Exception ex) {
            logger.error("Exception while extracting audio attributes.", ex);
        }

        return audioAttributes;
    }

}
