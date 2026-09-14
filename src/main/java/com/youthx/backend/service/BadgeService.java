package com.youthx.backend.service;


import com.youthx.backend.entity.Badge;
import com.youthx.backend.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public List<Badge> listAll() {
        return badgeRepository.findAll();
    }
}
