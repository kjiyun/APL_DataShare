package com.dtest.drools.global.s3;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class S3FileDownloadService {

    // s3에서 파일 다운로드
    public File downloadFile(String fileUrl, String fileName) throws IOException {
        URL url = new URL(fileUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    public File unzipFile(File zipFile, String extractFileName) throws IOException {
        File extractFile = new File(System.getProperty("java.io.tmpdir"), extractFileName);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
             FileOutputStream fos = new FileOutputStream(extractFile)) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals(extractFileName)) { // 원하는 파일만 추출
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    break;
                }
            }
        }
        return extractFile;
    }
}
