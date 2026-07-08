package com.rmsc.ai.repository;

import com.rmsc.ai.entity.EventEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link EventEmbedding}.
 *
 * <p>Native SQL queries are used for vector similarity search because
 * JPQL does not support pgvector operators. The {@code <=>} operator
 * computes cosine distance; we subtract from 1 to get cosine similarity.
 */
@Repository
public interface EventEmbeddingRepository extends JpaRepository<EventEmbedding, Long> {

    /**
     * Checks whether an embedding exists for the given event.
     *
     * @param eventId the EventHistory PK
     * @return true if an embedding record exists
     */
    boolean existsByEventHistoryId(Long eventId);

    /**
     * Retrieves the embedding associated with a specific event.
     *
     * @param eventId the EventHistory PK
     * @return an Optional containing the embedding, or empty
     */
    Optional<EventEmbedding> findByEventHistoryId(Long eventId);

    /**
     * Semantic similarity search using pgvector cosine distance operator.
     *
     * <p>The query:
     * <ol>
     *   <li>Computes {@code 1 - (embedding <=> queryVector)} for each row
     *       — i.e. converts cosine <em>distance</em> to cosine <em>similarity</em></li>
     *   <li>Filters by minimum similarity using HAVING</li>
     *   <li>Orders by similarity descending (most relevant first)</li>
     *   <li>Returns at most {@code topK} rows</li>
     * </ol>
     *
     * <p>The HNSW index created in V2 migration is used automatically by
     * the planner for this query pattern.
     *
     * @param queryVector    the embedding of the user's question as a formatted string, e.g. "[0.1,0.2,...]"
     * @param topK           maximum results to return
     * @param minSimilarity  minimum cosine similarity threshold
     * @return list of Object[] rows: [EventEmbedding, Double similarity]
     */
    @Query(value = """
            SELECT ee.id,
                   ee.event_id,
                   ee.embedding_text,
                   ee.embedding_model,
                   ee.created_at,
                   (1 - (ee.embedding <=> CAST(:queryVector AS vector))) AS similarity
            FROM   event_embedding ee
            WHERE  (1 - (ee.embedding <=> CAST(:queryVector AS vector))) >= :minSimilarity
            ORDER  BY similarity DESC
            LIMIT  :topK
            """, nativeQuery = true)
    List<Object[]> findTopKSimilar(
            @Param("queryVector") String queryVector,
            @Param("topK")        int topK,
            @Param("minSimilarity") double minSimilarity);
}
