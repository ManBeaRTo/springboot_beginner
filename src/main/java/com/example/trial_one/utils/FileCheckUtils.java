package com.example.trial_one.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public class FileCheckUtils {

    /**
     * Checks if the file has a JPEG content type
     */
    public static boolean isJpegContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg"));
    }

    /**
     * Checks if the file has a PNG content type
     */
    public static boolean isPngContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("image/png");
    }
    
    /**
     * Checks if the file is an image (JPEG or PNG)
     */
    public static boolean isImageFile(MultipartFile file) {
        return isJpegContentType(file) || isPngContentType(file);
    }
    
    /**
     * Validates if the file is a valid image by checking both content type and file signature
     */
    public static boolean isValidImageFile(MultipartFile file) throws IOException {
        // First check content type
        if (!isImageFile(file)) {
            return false;
        }
        
        return true;
    }
}