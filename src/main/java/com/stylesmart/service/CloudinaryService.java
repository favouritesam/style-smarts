package com.stylesmart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * CloudinaryService wraps Cloudinary SDK operations to upload and delete files.
 */
@Service
public class CloudinaryService {

    // Inject the configured Cloudinary bean
    @Autowired
    private Cloudinary cloudinary;

    /**
     * Uploads a MultipartFile to Cloudinary inside the 'profile_pictures' folder.
     *
     * @param file the multipart file uploaded by the user.
     * @return the secure URL of the uploaded image.
     * @throws IOException if network or file reading fails.
     */
    public String uploadImage(MultipartFile file) {

        try {

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of()
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }
    /**
     * Deletes an image from Cloudinary using its public ID.
     *
     * @param publicId the public ID of the resource in Cloudinary.
     * @throws IOException if the deletion command fails.
     */
    public void deleteImage(String publicId) throws IOException {
        if (publicId != null && !publicId.isEmpty()) {
            // Call Cloudinary API to destroy the asset
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * Helper method to extract the public ID from a full Cloudinary URL.
     * Necessary for identifying which remote asset to delete.
     *
     * @param url the full Cloudinary secure URL.
     * @return the public ID (including folder paths), or null if parsing fails.
     */
    public String extractPublicId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            // Example URL: https://res.cloudinary.com/demo/image/upload/v1570975200/profile_pictures/sample.jpg
            String searchKey = "/upload/";
            int uploadIndex = url.indexOf(searchKey);
            if (uploadIndex == -1) {
                return null;
            }

            // Get portion of URL following "/upload/" (e.g. "v1570975200/profile_pictures/sample.jpg")
            String postUpload = url.substring(uploadIndex + searchKey.length());

            // Skip the version number segment (e.g. "v1570975200/")
            if (postUpload.startsWith("v")) {
                int firstSlash = postUpload.indexOf('/');
                if (firstSlash != -1) {
                    postUpload = postUpload.substring(firstSlash + 1);
                }
            }

            // Strip the file extension (e.g. ".jpg") to get the clean public ID
            int lastDot = postUpload.lastIndexOf('.');
            if (lastDot != -1) {
                postUpload = postUpload.substring(0, lastDot);
            }

            return postUpload; // Returns "profile_pictures/sample"
        } catch (Exception e) {
            // Gracefully return null if formatting is unrecognized
            return null;
        }
    }
}
