package com.stylesmart.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that sets up the Cloudinary connection bean.
 * Credentials are loaded dynamically from application.properties.
 */
@Configuration
public class CloudinaryConfig {

    // Inject Cloudinary Cloud Name
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    // Inject Cloudinary API Key
    @Value("${cloudinary.api-key:}")
    private String apiKey;

    // Inject Cloudinary API Secret
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    /**
     * Creates and registers a Cloudinary bean with the application context.
     * 
     * @return Cloudinary instance configured with credentials.
     */
    @Bean
    public Cloudinary cloudinary() {
        // We set up a map of configuration credentials and initialize the SDK client
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
