-- =============================================================
-- V4__add_event_reference_id.sql
-- Adds the event_reference_id column for trace-based retrieval.
-- =============================================================

ALTER TABLE event_history
    ADD COLUMN event_reference_id VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_event_history_event_reference_id
    ON event_history (event_reference_id);

COMMENT ON COLUMN event_history.event_reference_id IS 'Correlation ID grouping all events in a single period/chain (e.g. evt001)';
