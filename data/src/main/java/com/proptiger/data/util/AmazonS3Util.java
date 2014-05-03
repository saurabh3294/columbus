package com.proptiger.data.util;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

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
public class AmazonS3Util {
    @Value("${bucket}")
    private String   bucket;

    @Value("${accessKeyId}")
    private String   accessKeyId;

    @Value("${secretAccessKey}")
    private String   secretAccessKey;

    private AmazonS3 amazonS3;

    @PostConstruct
    public void init() {
        ClientConfiguration config = new ClientConfiguration();
        config.withProtocol(Protocol.HTTP);
        config.setMaxErrorRetry(3);

        System.out.println("POSTCONSTRUCT " + this.accessKeyId + this.secretAccessKey);
        // AWSCredentials credentials = new
        // BasicAWSCredentials(this.accessKeyId,
        // this.secretAccessKey);
        // this.amazonS3 = new AmazonS3Client(credentials, config);
    }

    public AmazonS3Util() {
        ClientConfiguration config = new ClientConfiguration();
        config.withProtocol(Protocol.HTTP);
        config.setMaxErrorRetry(3);

        System.out.println("CONSTRUCTOR " + this.accessKeyId + this.secretAccessKey);
        AWSCredentials credentials = new BasicAWSCredentials(
                "AKIAI5FTEFLES7UMOD4A",
                "HMvOkDtE4OtZJFPkGozE7lEaFuKUWZbcjnNdWnSm");
        this.amazonS3 = new AmazonS3Client(credentials, config);
    }

    public void uploadFile(String pathInS3Bucket, File file) {
        System.out.println("PATH = " + pathInS3Bucket + " SIZE " + file.length());
        System.out.println("FINAL " + this.accessKeyId + this.secretAccessKey);
        this.amazonS3.putObject("im.proptiger-ws.com", pathInS3Bucket, file);
    }
}