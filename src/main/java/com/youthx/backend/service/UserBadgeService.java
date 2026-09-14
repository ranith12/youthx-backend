package com.youthx.backend.service;


import com.youthx.backend.entity.UserBadge;
import com.youthx.backend.entity.UserBadgeId;
import com.youthx.backend.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;

    public List<UserBadge> listEarnedByUser(UUID currentUserId) {
        return userBadgeRepository.findByIdUserId(currentUserId);
    }

    public UserBadge awardBadge(UUID currentUserId, Long badgeId) {
        UserBadgeId id = new UserBadgeId();
        id.setUserId(currentUserId);
        id.setBadgeId(badgeId);

        if (userBadgeRepository.existsById(id)) {
            throw new IllegalArgumentException("Badge already earned by user");
        }

        UserBadge userBadge = new UserBadge();
        userBadge.setId(id);
        userBadge.setEarnedAt(OffsetDateTime.now());

        return userBadgeRepository.save(userBadge);
    }
}
