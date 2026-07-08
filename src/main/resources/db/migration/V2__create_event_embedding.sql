-- =============================================================
-- V2__create_event_embedding.sql
-- Creates the semantic embedding table for RAG retrieval.
-- =============================================================

-- -----------------------------------------------------------
-- Table: event_embedding
-- Purpose: Stores the vector embedding and the business
--          sentence that was embedded for each ERP event.
--          VECTOR(768) supports models with 768 dimensions.
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS event_embedding
(
    id               BIGSERIAL       PRIMARY KEY,
    event_id         BIGINT          NOT NULL,
    embedding_text   TEXT            NOT NULL,
    embedding        VECTOR(768)     NOT NULL,
    embedding_model  VARCHAR(200)    NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_embedding_event
        FOREIGN KEY (event_id)
        REFERENCES event_history (id)
        ON DELETE CASCADE
);

-- Standard B-tree index for FK lookups
CREATE UNIQUE INDEX IF NOT EXISTS idx_event_embedding_event_id
    ON event_embedding (event_id);

-- HNSW index for fast approximate nearest-neighbour (ANN) cosine search
-- m=16 and ef_construction=64 are standard production starting values
CREATE INDEX IF NOT EXISTS idx_event_embedding_hnsw
    ON event_embedding
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

COMMENT ON TABLE  event_embedding                IS 'Semantic vector embeddings of ERP business events';
COMMENT ON COLUMN event_embedding.event_id       IS 'FK to event_history.id';
COMMENT ON COLUMN event_embedding.embedding_text IS 'Human-readable business sentence that was embedded';
COMMENT ON COLUMN event_embedding.embedding      IS 'VECTOR(768) produced by the embedding model';
COMMENT ON COLUMN event_embedding.embedding_model IS 'Model name used to produce the embedding';
