package com.youthx.backend.service;


import com.youthx.backend.entity.Comment;
import com.youthx.backend.exception.ResourceOwnershipException;
import com.youthx.backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment addComment(UUID currentUserId, Long postId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setContent(content);
        comment.setCreatedAt(OffsetDateTime.now());

        return commentRepository.save(comment);
    }

    public Page<Comment> listComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAscIdAsc(postId, pageable);
    }

    public void deleteComment(UUID currentUserId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));

        if (!comment.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this comment");
        }

        commentRepository.delete(comment);
    }
}
