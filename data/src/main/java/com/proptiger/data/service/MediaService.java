package com.proptiger.data.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.Media;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.util.AmazonS3Util;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */

public abstract class MediaService {
    protected Integer mediaTypeId = 2;

    @Value("${imageTempPath}")
    private String    tempDirPath;

    private File      tempDir;

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

            AmazonS3Util s3Util = new AmazonS3Util();
            s3Util.uploadFile("1/1/1/1/test", originalFile);

            return media;
        }
        catch (IOException e) {
            throw new ProAPIException(e);
        }
    }
}