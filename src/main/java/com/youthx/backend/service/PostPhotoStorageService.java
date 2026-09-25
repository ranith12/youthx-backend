package com.youthx.backend.service;

import com.youthx.backend.config.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Stores uploaded post photos on disk and exposes the URL used to display
 * them. The directory is configurable via {@code app.upload.dir}; files are
 * served back through the {@code /uploads/**} static resource mapping.
 *
 * File names are generated server-side from the validated content type, so a
 * client-supplied extension is never trusted.
 */
@Service
public class PostPhotoStorageService {

    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    private final StorageProperties storageProperties;

    public PostPhotoStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    /** Validates and persists the uploaded photo. Returns its public URL. */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Photo file is required");
        }

        String contentType = file.getContentType();
        String extension = ALLOWED_CONTENT_TYPES.get(
                contentType == null ? "" : contentType.toLowerCase(Locale.ROOT)
        );
        if (extension == null) {
            throw new IllegalArgumentException(
                    "Only JPEG, PNG, WEBP and GIF images are supported"
            );
        }

        Path directory = toPath();
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }

        String fileName = UUID.randomUUID() + extension;
        Path target = directory.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store photo file", e);
        }

        return "/uploads/" + fileName;
    }

    private Path toPath() {
        return Path.of(storageProperties.getDir()).toAbsolutePath().normalize();
    }
}