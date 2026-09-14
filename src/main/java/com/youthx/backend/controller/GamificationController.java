package com.youthx.backend.controller;


import com.youthx.backend.dto.BadgeResponse;
import com.youthx.backend.dto.ChallengeResponse;
import com.youthx.backend.dto.UserBadgeResponse;
import com.youthx.backend.dto.UserChallengeResponse;
import com.youthx.backend.dto.UpdateProgressRequest;
import com.youthx.backend.entity.Badge;
import com.youthx.backend.entity.Challenge;
import com.youthx.backend.entity.UserBadge;
import com.youthx.backend.entity.UserChallenge;
import com.youthx.backend.service.BadgeService;
import com.youthx.backend.service.ChallengeService;
import com.youthx.backend.service.UserBadgeService;
import com.youthx.backend.service.UserChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GamificationController {

    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/badges")
    public ResponseEntity<List<BadgeResponse>> listBadges() {
        List<BadgeResponse> badges = badgeService.listAll().stream()
                .map(this::toBadgeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/users/me/badges")
    public ResponseEntity<List<UserBadgeResponse>> myBadges() {
        UUID currentUserId = currentUserProvider.currentUserId();
        List<UserBadgeResponse> badges = userBadgeService.listEarnedByUser(currentUserId).stream()
                .map(this::toUserBadgeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeResponse>> listChallenges() {
        List<ChallengeResponse> challenges = challengeService.listAll().stream()
                .map(this::toChallengeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/users/me/challenges")
    public ResponseEntity<List<UserChallengeResponse>> myChallenges() {
        UUID currentUserId = currentUserProvider.currentUserId();
        List<UserChallengeResponse> entries = userChallengeService.listByUser(currentUserId).stream()
                .map(this::toUserChallengeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(entries);
    }

    @PostMapping("/challenges/{id}/join")
    public ResponseEntity<UserChallengeResponse> join(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(toUserChallengeResponse(userChallengeService.joinChallenge(currentUserId, id)));
    }

    @PutMapping("/challenges/{id}/progress")
    public ResponseEntity<UserChallengeResponse> updateProgress(@PathVariable Long id,
                                                                @Valid @RequestBody UpdateProgressRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();
        UserChallenge updated = userChallengeService.updateProgress(currentUserId, id, request.getProgress());
        return ResponseEntity.ok(toUserChallengeResponse(updated));
    }

    @PostMapping("/challenges/{id}/complete")
    public ResponseEntity<UserChallengeResponse> complete(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        UserChallenge completed = userChallengeService.completeChallenge(currentUserId, id);
        return ResponseEntity.ok(toUserChallengeResponse(completed));
    }

    private BadgeResponse toBadgeResponse(Badge badge) {
        BadgeResponse response = new BadgeResponse();
        response.setId(badge.getId());
        response.setName(badge.getName());
        response.setDescription(badge.getDescription());
        response.setIcon(badge.getIcon());
        return response;
    }

    private UserBadgeResponse toUserBadgeResponse(UserBadge userBadge) {
        UserBadgeResponse response = new UserBadgeResponse();
        response.setUserId(userBadge.getId().getUserId());
        response.setBadgeId(userBadge.getId().getBadgeId());
        response.setEarnedAt(userBadge.getEarnedAt());
        return response;
    }

    private ChallengeResponse toChallengeResponse(Challenge challenge) {
        ChallengeResponse response = new ChallengeResponse();
        response.setId(challenge.getId());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setType(challenge.getType());
        response.setXpReward(challenge.getXpReward());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        return response;
    }

    private UserChallengeResponse toUserChallengeResponse(UserChallenge userChallenge) {
        UserChallengeResponse response = new UserChallengeResponse();
        response.setUserId(userChallenge.getId().getUserId());
        response.setChallengeId(userChallenge.getId().getChallengeId());
        response.setProgress(userChallenge.getProgress());
        response.setIsCompleted(userChallenge.getIsCompleted());
        response.setCompletedAt(userChallenge.getCompletedAt());
        return response;
    }
}
