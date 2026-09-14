package com.youthx.backend.repository;


import com.youthx.backend.entity.UserChallenge;
import com.youthx.backend.entity.UserChallengeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, UserChallengeId> {

    List<UserChallenge> findByIdUserId(UUID userId);
}
