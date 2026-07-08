package com.rmsc.ai.repository;

import com.rmsc.ai.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link EventHistory}.
 *
 * <p>Provides out-of-the-box CRUD via {@link JpaRepository}.
 * Custom queries use JPQL to remain database-agnostic where possible.
 */
@Repository
public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

    /**
     * Retrieves all events that do NOT yet have an embedding record.
     *
     * <p>Used by the batch embedding endpoint {@code POST /events/embed/all}
     * to identify which events still need to be processed.
     *
     * @return list of un-embedded events ordered by creation time
     */
    @Query("""
            SELECT e FROM EventHistory e
            WHERE e.id NOT IN (
                SELECT em.eventHistory.id FROM EventEmbedding em
            )
            ORDER BY e.createdAt ASC
            """)
    List<EventHistory> findAllWithoutEmbedding();

    /**
     * Retrieves events for a specific organization, filtered by module.
     *
     * @param organizationId the tenant ID
     * @param moduleName     the ERP module name
     * @return matching events ordered by event time descending
     */
    @Query("""
            SELECT e FROM EventHistory e
            WHERE e.organizationId = :organizationId
              AND e.moduleName = :moduleName
            ORDER BY e.eventTime DESC
            """)
    List<EventHistory> findByOrganizationAndModule(
            @Param("organizationId") Long organizationId,
            @Param("moduleName") String moduleName);

    /**
     * Counts embedded vs un-embedded events for monitoring/health checks.
     *
     * @return count of events that have embeddings
     */
    @Query("""
            SELECT COUNT(e) FROM EventHistory e
            WHERE e.id IN (SELECT em.eventHistory.id FROM EventEmbedding em)
            """)
    long countEmbeddedEvents();
}
