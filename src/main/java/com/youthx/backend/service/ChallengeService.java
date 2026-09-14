package com.youthx.backend.service;


import com.youthx.backend.entity.Challenge;
import com.youthx.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public List<Challenge> listAll() {
        return challengeRepository.findAll();
    }
}
