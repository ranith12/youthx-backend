INSERT INTO expense_categories (name, icon) VALUES
    ('Food', '🍜'),
    ('Transport', '🚌'),
    ('Shopping', '🛍️'),
    ('Housing', '🏠'),
    ('Utilities', '💡'),
    ('Education', '📚'),
    ('Entertainment', '🎬'),
    ('Health', '💊'),
    ('Personal Care', '🧴'),
    ('Other', '✨');

INSERT INTO badges (name, description, icon) VALUES
    ('First Step', 'Create your first goal', '🌱'),
    ('Goal Crusher', 'Complete your first goal', '🎯'),
    ('Penny Wise', 'Log your first expense', '🪙'),
    ('Budget Pro', 'Track expenses for 7 days in a row', '📊'),
    ('Saver Extraordinaire', 'Reach your first savings goal', '🏦'),
    ('Challenge Champ', 'Complete your first challenge', '🏆'),
    ('Habit Hero', 'Maintain a 7-day habit streak', '🔥'),
    ('Rising Star', 'Reach 1000 XP', '⭐');

INSERT INTO challenges (title, description, type, xp_reward, start_date, end_date) VALUES
    ('Expense Spotter', 'Log at least one expense today', 'daily', 50, '2026-09-01', '2026-12-31'),
    ('No-Waste Tracker', 'Avoid an unplanned snack or drink purchase today', 'daily', 40, '2026-09-01', '2026-12-31'),
    ('Water Champion', 'Drink 8 glasses of water today', 'daily', 30, '2026-09-01', '2026-12-31'),
    ('Daily Habit Check', 'Complete all of today''s habits', 'daily', 60, '2026-09-01', '2026-12-31'),
    ('Budget Planner', 'Review and set next week''s budget', 'weekly', 120, '2026-09-01', '2026-12-31'),
    ('Step Up', 'Reach 30,000 steps this week', 'weekly', 100, '2026-09-01', '2026-12-31'),
    ('Goal Boost', 'Increase progress on a goal this week', 'weekly', 150, '2026-09-01', '2026-12-31'),
    ('Savings Streak', 'Save money at least 3 times this week', 'weekly', 200, '2026-09-01', '2026-12-31');