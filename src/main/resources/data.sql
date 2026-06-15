-- Create bets table
CREATE TABLE IF NOT EXISTS bets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    jackpot_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create jackpot table
CREATE TABLE IF NOT EXISTS jackpot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    initial_pool DECIMAL(19,2) NOT NULL,
    current_pool DECIMAL(19,2) NOT NULL,
    contribution_strategy VARCHAR(50) NOT NULL,
    reward_strategy VARCHAR(50) NOT NULL,
    contribution_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.05, -- NEW COLUMN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create jackpot_contribution table
CREATE TABLE IF NOT EXISTS jackpot_contribution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    jackpot_id BIGINT NOT NULL,
    stake_amount DECIMAL(19,2) NOT NULL,
    contribution_amount DECIMAL(19,2) NOT NULL,
    current_jackpot_amount DECIMAL(19,2) NOT NULL,
    percentage_used DECIMAL(5,2) NOT NULL,   -- e.g. 10.00 for 10%
    strategy_type VARCHAR(50) NOT NULL,      -- "FIXED" or "VARIABLE"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contribution_bet FOREIGN KEY (bet_id) REFERENCES bets(id) ON DELETE CASCADE,
    CONSTRAINT fk_contribution_jackpot FOREIGN KEY (jackpot_id) REFERENCES jackpot(id) ON DELETE CASCADE
);

-- Create jackpot_reward table
CREATE TABLE IF NOT EXISTS jackpot_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    jackpot_id BIGINT NOT NULL,
    reward_amount DECIMAL(19,2) NOT NULL,
    percentage_used DECIMAL(5,2) NOT NULL,   -- e.g. 20.00 for 20%
    strategy_type VARCHAR(50) NOT NULL,      -- "FIXED" or "VARIABLE"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_bet FOREIGN KEY (bet_id) REFERENCES bets(id) ON DELETE CASCADE,
    CONSTRAINT fk_reward_jackpot FOREIGN KEY (jackpot_id) REFERENCES jackpot(id) ON DELETE CASCADE
);

-- Seed jackpots with pools that guarantee winners and non-winners

-- Fixed reward jackpot: pool > 5000 triggers a win
INSERT INTO jackpot (id, name, initial_pool, current_pool, contribution_strategy, reward_strategy, contribution_percentage, created_at, updated_at)
VALUES (1, 'Fixed Reward Jackpot - Winner', 1000.00, 6000.00, 'FIXED', 'FIXED', 0.05, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Fixed reward jackpot: pool <= 5000 means no win
INSERT INTO jackpot (id, name, initial_pool, current_pool, contribution_strategy, reward_strategy, contribution_percentage, created_at, updated_at)
VALUES (2, 'Fixed Reward Jackpot - No Winner', 1000.00, 4000.00, 'FIXED', 'FIXED', 0.05, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Variable reward jackpot: pool divisible by 7 triggers a win
INSERT INTO jackpot (id, name, initial_pool, current_pool, contribution_strategy, reward_strategy, contribution_percentage, created_at, updated_at)
VALUES (3, 'Variable Reward Jackpot - Winner', 1000.00, 7000.00, 'VARIABLE', 'VARIABLE', 0.10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Variable reward jackpot: pool not divisible by 7 means no win
INSERT INTO jackpot (id, name, initial_pool, current_pool, contribution_strategy, reward_strategy, contribution_percentage, created_at, updated_at)
VALUES (4, 'Variable Reward Jackpot - No Winner', 1000.00, 7500.00, 'VARIABLE', 'VARIABLE', 0.10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed bets tied to those jackpots
--INSERT INTO bets (id, user_id, jackpot_id, amount, created_at)
--VALUES (1, 101, 1, 100.00, CURRENT_TIMESTAMP); -- Bet for Fixed Reward Jackpot - Winner

--INSERT INTO bets (id, user_id, jackpot_id, amount, created_at)
--VALUES (2, 102, 2, 200.00, CURRENT_TIMESTAMP); -- Bet for Fixed Reward Jackpot - No Winner

--INSERT INTO bets (id, user_id, jackpot_id, amount, created_at)
--VALUES (3, 103, 3, 150.00, CURRENT_TIMESTAMP); -- Bet for Variable Reward Jackpot - Winner

--INSERT INTO bets (id, user_id, jackpot_id, amount, created_at)
--VALUES (4, 104, 4, 250.00, CURRENT_TIMESTAMP); -- Bet for Variable Reward Jackpot - No Winner
