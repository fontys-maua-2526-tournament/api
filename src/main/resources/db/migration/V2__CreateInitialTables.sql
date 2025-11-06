-- ============================================================
-- FULL DATABASE CREATION SCRIPT (Version 2)
-- Drops all old tables and recreates everything from scratch
-- ============================================================

-- Disable foreign key checks to allow dropping in any order
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- STEP 1: Drop all existing tables (any order)
-- ============================================================

DROP TABLE IF EXISTS user_team;
DROP TABLE IF EXISTS team_tournament;
DROP TABLE IF EXISTS team_organization;
DROP TABLE IF EXISTS match_entity;
DROP TABLE IF EXISTS organization;
DROP TABLE IF EXISTS tournament;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS coach;  -- In case the old schema still has this

-- ============================================================
-- STEP 2: Create all tables from scratch
-- ============================================================

-- Create user table
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    phone_number VARCHAR(255),
    date_of_birth DATE,
    user_role VARCHAR(50)
    );

-- Create team table
CREATE TABLE IF NOT EXISTS team (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(50) NOT NULL,
    CONSTRAINT chk_team_name_length CHECK (LENGTH(name) >= 2)
    );

-- Create tournament table
CREATE TABLE IF NOT EXISTS tournament (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    organizer_id BIGINT NULL,
    CONSTRAINT chk_tournament_name_length CHECK (LENGTH(name) >= 2),
    CONSTRAINT chk_tournament_address_length CHECK (LENGTH(address) >= 2),
    CONSTRAINT fk_tournament_organizer FOREIGN KEY (organizer_id)
    REFERENCES user(id)
    ON DELETE SET NULL
    );

-- Create organization table
CREATE TABLE IF NOT EXISTS organization (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(255),
    parent_id BIGINT NULL,
    CONSTRAINT fk_organization_parent FOREIGN KEY (parent_id)
    REFERENCES organization(id)
    ON DELETE SET NULL
    );

-- Create match table
CREATE TABLE IF NOT EXISTS match_entity (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            tournament_id BIGINT NULL,
                                            round INT,
                                            team1_id BIGINT NULL,
                                            team2_id BIGINT NULL,
                                            team1_score INT,
                                            team2_score INT,
                                            CONSTRAINT fk_match_tournament FOREIGN KEY (tournament_id)
    REFERENCES tournament(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_match_team1 FOREIGN KEY (team1_id)
    REFERENCES team(id)
    ON DELETE SET NULL,
    CONSTRAINT fk_match_team2 FOREIGN KEY (team2_id)
    REFERENCES team(id)
    ON DELETE SET NULL
    );

-- Create user_team junction table (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_team (
                                         user_id BIGINT NOT NULL,
                                         team_id BIGINT NOT NULL,
                                         PRIMARY KEY (user_id, team_id),
    CONSTRAINT fk_user_team_user FOREIGN KEY (user_id)
    REFERENCES user(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_user_team_team FOREIGN KEY (team_id)
    REFERENCES team(id)
    ON DELETE CASCADE
    );

-- Create team_tournament junction table (Many-to-Many)
CREATE TABLE IF NOT EXISTS team_tournament (
                                               team_id BIGINT NOT NULL,
                                               tournament_id BIGINT NOT NULL,
                                               PRIMARY KEY (team_id, tournament_id),
    CONSTRAINT fk_team_tournament_team FOREIGN KEY (team_id)
    REFERENCES team(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_team_tournament_tournament FOREIGN KEY (tournament_id)
    REFERENCES tournament(id)
    ON DELETE CASCADE
    );

-- Create team_organization junction table (Many-to-Many)
CREATE TABLE IF NOT EXISTS team_organization (
                                                 team_id BIGINT NOT NULL,
                                                 organization_id BIGINT NOT NULL,
                                                 PRIMARY KEY (team_id, organization_id),
    CONSTRAINT fk_team_organization_team FOREIGN KEY (team_id)
    REFERENCES team(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_team_organization_organization FOREIGN KEY (organization_id)
    REFERENCES organization(id)
    ON DELETE CASCADE
    );

-- ============================================================
-- STEP 3: Finalize
-- ============================================================

SET FOREIGN_KEY_CHECKS = 1;