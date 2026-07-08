-- =============================================================
-- V3__update_embedding_dimension_1536.sql
-- Updates the embedding vector column from VECTOR(768)
-- to VECTOR(1536) to match openai/text-embedding-3-small output.
-- =============================================================

-- Drop the HNSW index first (required before altering vector dimensions)
DROP INDEX IF EXISTS idx_event_embedding_hnsw;

-- Drop the old vector_store table created by Spring AI (if it has wrong dims)
DROP TABLE IF EXISTS vector_store;

-- Alter the column dimension
ALTER TABLE event_embedding
    ALTER COLUMN embedding TYPE VECTOR(1536)
    USING embedding::text::VECTOR(1536);

-- Recreate HNSW index for 1536-dimension cosine search
CREATE INDEX IF NOT EXISTS idx_event_embedding_hnsw
    ON event_embedding
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

COMMENT ON COLUMN event_embedding.embedding IS 'VECTOR(1536) produced by openai/text-embedding-3-small';
