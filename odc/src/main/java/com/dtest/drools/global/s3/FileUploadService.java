package com.dtest.drools.global.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

// Presigned URL을 생성해주는 코드
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final S3Client s3Client;
    private final AmazonS3 amazonS3;
    private final String bucketName = "apl-rule-bucket"; //TODO: application.yml에 의미 정의되어있음
    private final String region = "ap-northeast-2";

    public String generatePresignUrl(String filePath, String bucketName, HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);  // 유효시간은 10분으로 설정
        return amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(),httpMethod).toString();
    }

    public String uploadFile(File file, String s3Key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            return "https://" + bucketName + "." + region + "/" + s3Key;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CANNOT_UPLOAD_S3);
        }
    }
}
