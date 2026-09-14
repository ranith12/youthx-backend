package com.youthx.backend.repository;


import com.youthx.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostIdAndUserId(Long postId, UUID userId);

    Optional<Like> findByPostIdAndUserId(Long postId, UUID userId);

    long countByPostId(Long postId);
}
