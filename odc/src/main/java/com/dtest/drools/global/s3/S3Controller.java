package com.dtest.drools.global.s3;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final FileUploadService fileUploadService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @GetMapping("/presigned-url")
    public ResponseEntity<String> getSignedUrls(@RequestParam String extension) {
        // 확장자명에 따라 PresignedUrl 반환
        return ResponseEntity.ok(
                fileUploadService.generatePresignUrl(
                        UUID.randomUUID() + "." + extension, bucketName, HttpMethod.PUT));
    }
}
