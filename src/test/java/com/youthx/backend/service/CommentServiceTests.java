package com.youthx.backend.service;

import com.youthx.backend.entity.Comment;
import com.youthx.backend.repository.CommentRepository;
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

class CommentServiceTests {

    private CommentRepository commentRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentService = new CommentService(commentRepository);
    }

    @Test
    void listCommentsDelegatesToDeterministicallyOrderedFinder() {
        Long postId = 42L;
        Pageable pageable = PageRequest.of(1, 10);
        Page<Comment> expected = new PageImpl<>(List.of());
        when(commentRepository.findByPostIdOrderByCreatedAtAscIdAsc(postId, pageable)).thenReturn(expected);

        Page<Comment> actual = commentService.listComments(postId, pageable);

        assertSame(expected, actual);
        verify(commentRepository).findByPostIdOrderByCreatedAtAscIdAsc(postId, pageable);
    }
}
