package com.dtest.drools.global.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.Date;

@RequiredArgsConstructor
public class S3SignedUrlGenerator {
    private final AmazonS3 s3Client;

    public URL generateSignedUrl(String bucketName, String key, int expirationInSeconds) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + (expirationInSeconds * 1000));

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
