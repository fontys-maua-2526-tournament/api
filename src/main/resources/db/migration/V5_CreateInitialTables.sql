-- 1. Fix the MatchEntity table (Missing columns from first error)
-- We add 'status' (VARCHAR) and score columns (INT)
ALTER TABLE match_entity 
ADD COLUMN status VARCHAR(255),
ADD COLUMN team1score INT,
ADD COLUMN team2score INT;

-- 2. Fix Organization Schema (Missing table from last error)
-- If the table 'organization' existed but was named wrong, we rename it.
-- If it didn't exist at all, this logic ensures we end up with 'organization_entity'.

-- (Optional) If you had a table named 'organization', rename it first:
-- RENAME TABLE organization TO organization_entity;

-- Ensure 'organization_entity' exists with the correct structure
CREATE TABLE IF NOT EXISTS organization_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    parent_id BIGINT,
    CONSTRAINT fk_organization_parent FOREIGN KEY (parent_id) REFERENCES organization_entity (id)
);

-- 3. Ensure the Many-to-Many join table exists for Team <-> Organization
CREATE TABLE IF NOT EXISTS team_organization (
    team_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (team_id, organization_id),
    CONSTRAINT fk_team_organization_team 
        FOREIGN KEY (team_id) REFERENCES team (id),
    CONSTRAINT fk_team_organization_organization 
        FOREIGN KEY (organization_id) REFERENCES organization_entity (id) 
        ON DELETE CASCADE
);