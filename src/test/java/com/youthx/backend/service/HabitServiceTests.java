package com.youthx.backend.service;

import com.youthx.backend.entity.Habit;
import com.youthx.backend.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HabitServiceTests {

    private HabitRepository habitRepository;
    private HabitService habitService;
    private final UUID owner = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        habitRepository = mock(HabitRepository.class);
        habitService = new HabitService(habitRepository);
    }

    @Test
    void completeIncrementsStreakAndStampsTodayWhenNotYetCompleted() {
        Habit habit = habit(owner, 4, null);
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenAnswer(i -> i.getArgument(0));

        Habit result = habitService.complete(owner, habit.getId());

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getCurrentStreak());
        assertEquals(LocalDate.now(), captor.getValue().getLastCompletedAt());
        assertEquals(5, result.getCurrentStreak());
    }

    @Test
    void completeIsIdempotentForSameDay() {
        Habit habit = habit(owner, 4, LocalDate.now());
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));

        Habit result = habitService.complete(owner, habit.getId());

        verify(habitRepository, never()).save(any(Habit.class));
        assertEquals(4, result.getCurrentStreak());
    }

    @Test
    void uncompleteRevertsTodayAndMirrorsStreak() {
        Habit habit = habit(owner, 5, LocalDate.now());
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenAnswer(i -> i.getArgument(0));

        habitService.uncomplete(owner, habit.getId());

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertEquals(4, captor.getValue().getCurrentStreak());
        assertNull(captor.getValue().getLastCompletedAt());
    }

    @Test
    void uncompleteNeverDropsBelowZero() {
        Habit habit = habit(owner, 0, LocalDate.now());
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenAnswer(i -> i.getArgument(0));

        habitService.uncomplete(owner, habit.getId());

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getCurrentStreak());
    }

    @Test
    void uncompleteIsNoOpWhenNotCompletedToday() {
        Habit habit = habit(owner, 3, LocalDate.now().minusDays(1));
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));

        Habit result = habitService.uncomplete(owner, habit.getId());

        verify(habitRepository, never()).save(any(Habit.class));
        assertEquals(3, result.getCurrentStreak());
    }

    @Test
    void rejectsOtherUsers() {
        Habit habit = habit(owner, 1, null);
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));

        assertThrows(com.youthx.backend.exception.ResourceOwnershipException.class,
                () -> habitService.complete(UUID.randomUUID(), habit.getId()));
        verify(habitRepository, never()).save(any(Habit.class));
    }

    private Habit habit(UUID userId, int streak, LocalDate lastCompleted) {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setUserId(userId);
        habit.setName("Read");
        habit.setFrequency("daily");
        habit.setCurrentStreak(streak);
        habit.setLastCompletedAt(lastCompleted);
        return habit;
    }
}