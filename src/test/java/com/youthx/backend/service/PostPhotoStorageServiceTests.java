package com.youthx.backend.service;

import com.youthx.backend.config.StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostPhotoStorageServiceTests {

    private PostPhotoStorageService service;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setDir("target/test-uploads");
        service = new PostPhotoStorageService(properties);
    }

    @Test
    void storesJpegAndReturnsUploadUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        String url = service.store(file);

        assertTrue(url.startsWith("/uploads/"));
        assertTrue(url.endsWith(".jpg"));
    }

    @Test
    void acceptsPngWebpAndGif() {
        assertTrue(service.store(file("image/png")).endsWith(".png"));
        assertTrue(service.store(file("image/webp")).endsWith(".webp"));
        assertTrue(service.store(file("image/gif")).endsWith(".gif"));
    }

    @Test
    void rejectsUnsupportedContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "clip.txt", "text/plain", "hi".getBytes());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> service.store(file));

        assertTrue(ex.getMessage().contains("JPEG"));
    }

    @Test
    void rejectsClientFakedJpegExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "virus.jpg", "text/plain", "boom".getBytes());

        assertThrows(IllegalArgumentException.class, () -> service.store(file));
    }

    @Test
    void rejectsMissingOrEmptyFile() {
        assertThrows(IllegalArgumentException.class, () -> service.store(null));
        assertThrows(IllegalArgumentException.class,
                () -> service.store(new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[]{})));
    }

    private MockMultipartFile file(String contentType) {
        return new MockMultipartFile(
                "file", "photo.img", contentType, new byte[]{1});
    }
}