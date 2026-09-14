package com.youthx.backend.service;


import com.youthx.backend.entity.Challenge;
import com.youthx.backend.entity.User;
import com.youthx.backend.entity.UserChallenge;
import com.youthx.backend.entity.UserChallengeId;
import com.youthx.backend.exception.DuplicateResourceException;
import com.youthx.backend.repository.ChallengeRepository;
import com.youthx.backend.repository.UserChallengeRepository;
import com.youthx.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public UserChallenge joinChallenge(UUID currentUserId, Long challengeId) {
        UserChallengeId id = new UserChallengeId();
        id.setUserId(currentUserId);
        id.setChallengeId(challengeId);

        if (userChallengeRepository.existsById(id)) {
            throw new DuplicateResourceException("User already joined this challenge");
        }

        UserChallenge userChallenge = new UserChallenge();
        userChallenge.setId(id);
        userChallenge.setProgress(0);
        userChallenge.setIsCompleted(false);

        return userChallengeRepository.save(userChallenge);
    }

    public UserChallenge updateProgress(UUID currentUserId, Long challengeId, int progress) {
        UserChallenge userChallenge = findOwned(currentUserId, challengeId);
        userChallenge.setProgress(progress);
        return userChallengeRepository.save(userChallenge);
    }

    @Transactional
    public UserChallenge completeChallenge(UUID currentUserId, Long challengeId) {
        UserChallenge userChallenge = findOwned(currentUserId, challengeId);

        if (Boolean.TRUE.equals(userChallenge.getIsCompleted())) {
            return userChallenge;
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new NoSuchElementException("Challenge not found with id: " + challengeId));

        userChallenge.setIsCompleted(true);
        userChallenge.setCompletedAt(OffsetDateTime.now());
        userChallengeRepository.save(userChallenge);

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + currentUserId));
        int xp = user.getXpPoints() == null ? 0 : user.getXpPoints();
        user.setXpPoints(xp + challenge.getXpReward());
        userRepository.save(user);

        return userChallenge;
    }

    public List<UserChallenge> listByUser(UUID currentUserId) {
        return userChallengeRepository.findByIdUserId(currentUserId);
    }

    private UserChallenge findOwned(UUID currentUserId, Long challengeId) {
        UserChallengeId id = new UserChallengeId();
        id.setUserId(currentUserId);
        id.setChallengeId(challengeId);

        UserChallenge userChallenge = userChallengeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User challenge not found for user: " + currentUserId + " challenge: " + challengeId));

        if (!userChallenge.getId().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("User does not own this challenge entry");
        }

        return userChallenge;
    }
}
