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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contribution_bet FOREIGN KEY (bet_id) REFERENCES bets(id),
    CONSTRAINT fk_contribution_jackpot FOREIGN KEY (jackpot_id) REFERENCES jackpot(id)
);

-- Create jackpot_reward table
CREATE TABLE IF NOT EXISTS jackpot_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    jackpot_id BIGINT NOT NULL,
    reward_amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_bet FOREIGN KEY (bet_id) REFERENCES bets(id),
    CONSTRAINT fk_reward_jackpot FOREIGN KEY (jackpot_id) REFERENCES jackpot(id)
);

-- Seed initial jackpot
INSERT INTO jackpot (name, initial_pool, current_pool, contribution_strategy, reward_strategy, created_at, updated_at)
VALUES ('Mega Jackpot', 1000.00, 1000.00, 'FIXED', 'VARIABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
