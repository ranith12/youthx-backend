package com.youthx.backend.service;

import com.youthx.backend.entity.Post;
import com.youthx.backend.repository.PostPhotoRepository;
import com.youthx.backend.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceTests {

    private PostRepository postRepository;
    private PostPhotoRepository postPhotoRepository;
    private PostPhotoStorageService postPhotoStorageService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postPhotoRepository = mock(PostPhotoRepository.class);
        postPhotoStorageService = mock(PostPhotoStorageService.class);
        postService = new PostService(postRepository, postPhotoRepository, postPhotoStorageService);
    }

    @Test
    void getFeedDelegatesToDeterministicallyOrderedFinder() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> expected = new PageImpl<>(List.of());
        when(postRepository.findAllByOrderByCreatedAtDescIdDesc(pageable)).thenReturn(expected);

        Page<Post> actual = postService.getFeed(pageable);

        assertSame(expected, actual);
        verify(postRepository).findAllByOrderByCreatedAtDescIdDesc(pageable);
    }
}
