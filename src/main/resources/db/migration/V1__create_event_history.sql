-- =============================================================
-- V1__create_event_history.sql
-- Creates the core ERP event history table.
-- =============================================================

-- Enable pgvector extension (must run once per database)
CREATE EXTENSION IF NOT EXISTS vector;

-- -----------------------------------------------------------
-- Table: event_history
-- Purpose: Persists every ERP business event that occurs
--          across all modules (Production, Inventory, etc.)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS event_history
(
    id               BIGSERIAL       PRIMARY KEY,
    event_type       VARCHAR(100)    NOT NULL,
    module_name      VARCHAR(100)    NOT NULL,
    reference_type   VARCHAR(100),
    reference_id     VARCHAR(255),
    description      TEXT,
    status           VARCHAR(50),
    organization_id  BIGINT,
    user_id          BIGINT,
    event_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    metadata         JSONB,
    created_by       VARCHAR(100),
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Indexes for common query filters
CREATE INDEX IF NOT EXISTS idx_event_history_event_type     ON event_history (event_type);
CREATE INDEX IF NOT EXISTS idx_event_history_module_name    ON event_history (module_name);
CREATE INDEX IF NOT EXISTS idx_event_history_reference_id   ON event_history (reference_id);
CREATE INDEX IF NOT EXISTS idx_event_history_organization   ON event_history (organization_id);
CREATE INDEX IF NOT EXISTS idx_event_history_event_time     ON event_history (event_time DESC);
CREATE INDEX IF NOT EXISTS idx_event_history_status         ON event_history (status);

COMMENT ON TABLE  event_history                IS 'ERP business events from all modules';
COMMENT ON COLUMN event_history.event_type     IS 'Type of business event (e.g. PRODUCTION_PLAN_SYNCED)';
COMMENT ON COLUMN event_history.module_name    IS 'ERP module that generated the event';
COMMENT ON COLUMN event_history.reference_type IS 'Entity type referenced (e.g. PRODUCTION_PLAN, ASN)';
COMMENT ON COLUMN event_history.reference_id   IS 'Business reference ID (e.g. PP1001, ASN450)';
COMMENT ON COLUMN event_history.metadata       IS 'Additional event context as JSONB';
