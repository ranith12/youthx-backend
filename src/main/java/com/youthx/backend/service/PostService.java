package com.youthx.backend.service;


import com.youthx.backend.entity.Post;
import com.youthx.backend.entity.PostPhoto;
import com.youthx.backend.repository.PostPhotoRepository;
import com.youthx.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostPhotoRepository postPhotoRepository;

    @Transactional
    public Post createPost(UUID currentUserId, String content, List<String> photoUrls) {
        Post post = new Post();
        post.setUserId(currentUserId);
        post.setContent(content);
        post.setCreatedAt(OffsetDateTime.now());

        Post savedPost = postRepository.save(post);

        if (photoUrls != null) {
            int displayOrder = 0;
            for (String photoUrl : photoUrls) {
                PostPhoto photo = new PostPhoto();
                photo.setPostId(savedPost.getId());
                photo.setPhotoUrl(photoUrl);
                photo.setDisplayOrder(displayOrder++);
                photo.setCreatedAt(OffsetDateTime.now());
                postPhotoRepository.save(photo);
            }
        }

        return savedPost;
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
    }

    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional
    public Post updatePost(UUID currentUserId, Long postId, String content) {
        Post post = getPost(postId);

        if (!post.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("User does not own this post");
        }

        post.setContent(content);
        post.setUpdatedAt(OffsetDateTime.now());

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(UUID currentUserId, Long postId) {
        Post post = getPost(postId);

        if (!post.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("User does not own this post");
        }

        postRepository.delete(post);
    }
}
