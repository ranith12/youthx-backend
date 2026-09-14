package com.youthx.backend.service;


import com.youthx.backend.entity.Like;
import com.youthx.backend.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void toggleLike(UUID currentUserId, Long postId) {
        if (likeRepository.existsByPostIdAndUserId(postId, currentUserId)) {
            Like like = likeRepository.findByPostIdAndUserId(postId, currentUserId)
                    .orElseThrow();
            likeRepository.delete(like);
        } else {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(currentUserId);
            like.setCreatedAt(OffsetDateTime.now());
            likeRepository.save(like);
        }
    }
}
