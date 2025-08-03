package com.audio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


public interface FileStorageService {
    String uploadFile(MultipartFile file, String objectName) throws Exception;
    void deleteFile(String objectName) throws Exception;
    InputStream getFile(String objectName) throws Exception;
}
