package com.youthx.backend.service;

import com.youthx.backend.entity.SavingGoal;
import com.youthx.backend.repository.SavingGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingGoalService {

    private final SavingGoalRepository savingGoalRepository;

    public SavingGoal create(
            UUID currentUserId,
            SavingGoal savingGoal
    ) {

        savingGoal.setUserId(currentUserId);
        savingGoal.setCreatedAt(OffsetDateTime.now());

        if (savingGoal.getCurrentAmount() == null) {
            savingGoal.setCurrentAmount(BigDecimal.ZERO);
        }

        return savingGoalRepository.save(savingGoal);
    }

    public SavingGoal deposit(
            UUID currentUserId,
            Long savingGoalId,
            BigDecimal amount
    ) {

        SavingGoal savingGoal =
                savingGoalRepository.findById(savingGoalId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Saving goal not found with id: "
                                                + savingGoalId
                                )
                        );

        if (!savingGoal.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "User does not own this saving goal"
            );
        }

        BigDecimal current =
                savingGoal.getCurrentAmount() == null
                        ? BigDecimal.ZERO
                        : savingGoal.getCurrentAmount();

        savingGoal.setCurrentAmount(
                current.add(amount)
        );

        return savingGoalRepository.save(savingGoal);
    }

    public List<SavingGoal> listByUser(
            UUID currentUserId
    ) {

        return savingGoalRepository.findByUserId(currentUserId);
    }

    public SavingGoal update(
            UUID currentUserId,
            Long savingGoalId,
            SavingGoal updatedSavingGoal
    ) {

        SavingGoal existingSavingGoal =
                savingGoalRepository.findById(savingGoalId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Saving goal not found with id: "
                                                + savingGoalId
                                )
                        );

        if (!existingSavingGoal.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "User does not own this saving goal"
            );
        }

        existingSavingGoal.setName(
                updatedSavingGoal.getName()
        );

        existingSavingGoal.setTargetAmount(
                updatedSavingGoal.getTargetAmount()
        );

        return savingGoalRepository.save(
                existingSavingGoal
        );
    }

    public void delete(
            UUID currentUserId,
            Long savingGoalId
    ) {

        SavingGoal savingGoal =
                savingGoalRepository.findById(savingGoalId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Saving goal not found with id: "
                                                + savingGoalId
                                )
                        );

        if (!savingGoal.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "User does not own this saving goal"
            );
        }

        savingGoalRepository.delete(savingGoal);
    }
}