-- GenAI Reward Bot Database Schema for Oracle
-- Drop existing sequences and tables if they exist (for development)
DROP SEQUENCE IF EXISTS user_sequence;
DROP SEQUENCE IF EXISTS card_sequence;
DROP SEQUENCE IF EXISTS reward_sequence;

DROP TABLE IF EXISTS reward_points CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create sequences
CREATE SEQUENCE user_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE card_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE reward_sequence START WITH 1 INCREMENT BY 1;

-- Create users table
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    mobile_number VARCHAR2(15) NOT NULL UNIQUE,
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    email VARCHAR2(100),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cards table
CREATE TABLE cards (
    id NUMBER(19) PRIMARY KEY,
    card_number VARCHAR2(20) NOT NULL UNIQUE,
    card_type VARCHAR2(50),
    vendor_name VARCHAR2(100) NOT NULL,
    vendor_code VARCHAR2(10),
    card_balance NUMBER(15,2) DEFAULT 0,
    currency VARCHAR2(3) DEFAULT 'INR',
    is_active NUMBER(1) DEFAULT 1,
    expiry_date TIMESTAMP,
    cashback_rate NUMBER(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id NUMBER(19) NOT NULL,
    CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create reward_points table
CREATE TABLE reward_points (
    id NUMBER(19) PRIMARY KEY,
    points_earned NUMBER(10) NOT NULL,
    points_used NUMBER(10) DEFAULT 0,
    points_available NUMBER(10) NOT NULL,
    earning_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP,
    source VARCHAR2(100),
    description VARCHAR2(255),
    transaction_id VARCHAR2(50),
    is_expired NUMBER(1) DEFAULT 0,
    point_value NUMBER(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    card_id NUMBER(19) NOT NULL,
    CONSTRAINT fk_reward_points_card FOREIGN KEY (card_id) REFERENCES cards(id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_mobile ON users(mobile_number);
CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_vendor ON cards(vendor_code);
CREATE INDEX idx_reward_points_card_id ON reward_points(card_id);
CREATE INDEX idx_reward_points_expiry ON reward_points(expiry_date);
CREATE INDEX idx_reward_points_earning_date ON reward_points(earning_date);

-- Add comments for documentation
COMMENT ON TABLE users IS 'User information with mobile number as primary identifier';
COMMENT ON TABLE cards IS 'Credit/Debit/Loyalty cards from various vendors';
COMMENT ON TABLE reward_points IS 'Reward points earned on each card with expiry tracking';

COMMENT ON COLUMN users.mobile_number IS 'Unique mobile number for user authentication';
COMMENT ON COLUMN cards.vendor_code IS 'Short code for vendor (HDFC, AMZN, FLIP, etc.)';
COMMENT ON COLUMN reward_points.points_available IS 'Points available for redemption (earned - used)';
COMMENT ON COLUMN reward_points.point_value IS 'Monetary value of each point in INR'; 