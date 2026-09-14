package com.youthx.backend.repository;


import com.youthx.backend.entity.UserBadge;
import com.youthx.backend.entity.UserBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {

    List<UserBadge> findByIdUserId(UUID userId);
}
