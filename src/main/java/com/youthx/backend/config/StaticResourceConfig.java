package com.youthx.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Serves uploaded post photos from disk through the {@code /uploads/**} URL
 * prefix. The directory is relative to the working directory by default and
 * can be overridden with {@code app.upload.dir}.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    public StaticResourceConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absoluteUploadDir =
                Path.of(storageProperties.getDir()).toAbsolutePath().normalize()
                        .toString()
                        .replace('\\', '/');

        if (!absoluteUploadDir.endsWith("/")) {
            absoluteUploadDir += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadDir);
    }
}