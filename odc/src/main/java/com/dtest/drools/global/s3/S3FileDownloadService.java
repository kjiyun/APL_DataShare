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

public File unzipFile(File zipFile, String targetFileName) throws IOException {
    byte[] buffer = new byte[1024];
    File destDir = new File(zipFile.getParent(), "unzipped");
    if (!destDir.exists()) destDir.mkdirs();

    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = new File(destDir, zipEntry.getName());
            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            if (newFile.getName().equals(targetFileName)) {
                return newFile;
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
    }
    throw new FileNotFoundException("File " + targetFileName + " not found in ZIP");
}
}
