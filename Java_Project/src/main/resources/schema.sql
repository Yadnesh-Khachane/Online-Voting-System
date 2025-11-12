-- Create database and tables
CREATE DATABASE IF NOT EXISTS voting_system;
USE voting_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('VOTER', 'ADMIN') DEFAULT 'VOTER',
    has_voted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Candidates table
CREATE TABLE IF NOT EXISTS candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    party VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    votes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Votes table
CREATE TABLE IF NOT EXISTS votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    candidate_id INT NOT NULL,
    position VARCHAR(100) NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

-- Insert default admin and sample data
INSERT IGNORE INTO users (username, password, email, role) VALUES 
('admin', 'admin123', 'admin@voting.com', 'ADMIN'),
('voter1', 'pass123', 'voter1@email.com', 'VOTER'),
('voter2', 'pass123', 'voter2@email.com', 'VOTER');

INSERT IGNORE INTO candidates (name, party, position) VALUES 
('John Smith', 'Democratic Party', 'President'),
('Jane Doe', 'Republican Party', 'President'),
('Mike Johnson', 'Green Party', 'President'),
('Sarah Wilson', 'Democratic Party', 'Senator'),
('Robert Brown', 'Republican Party', 'Senator');