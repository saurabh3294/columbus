package com.proptiger.data.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * 
 * @author azi
 * 
 */
@Component
public class AmazonS3Util {
    @Value("${bucket}")
    private String  bucket;

    @Value("${accessKeyId}")
    private String  accessKeyId;

    @Value("${secretAccessKey}")
    private String  secretAccessKey;

    private Integer maxErrorRetry = 3;

    private AmazonS3 getS3Instance() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.withProtocol(Protocol.HTTP);
        clientConfiguration.setMaxErrorRetry(maxErrorRetry);

        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return new AmazonS3Client(credentials, clientConfiguration);
    }

    public void uploadFile(String pathInS3Bucket, File file) {
        AmazonS3 amazonS3 = getS3Instance();
        amazonS3.putObject(bucket, pathInS3Bucket, file);
    }
}