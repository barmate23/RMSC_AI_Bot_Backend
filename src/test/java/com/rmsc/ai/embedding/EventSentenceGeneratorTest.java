package com.rmsc.ai.embedding;

import com.rmsc.ai.entity.EventHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link EventSentenceGenerator}.
 *
 * <p>Tests verify that each event type produces a grammatically correct,
 * semantically meaningful business sentence. Edge cases (null fields,
 * missing metadata) are also covered.
 *
 * <p>No spring context required — this is a pure unit test.
 */
@DisplayName("EventSentenceGenerator")
class EventSentenceGeneratorTest {

    private EventSentenceGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new EventSentenceGenerator();
    }

    // =========================================================================
    // Null Safety
    // =========================================================================

    @Test
    @DisplayName("should throw NullPointerException for null event")
    void shouldThrowForNullEvent() {
        assertThatThrownBy(() -> generator.generate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("event must not be null");
    }

    // =========================================================================
    // PRODUCTION MODULE
    // =========================================================================

    @Nested
    @DisplayName("Production Module Events")
    class ProductionModuleTests {

        @Test
        @DisplayName("PRODUCTION_PLAN_SYNCED should generate correct sentence")
        void productionPlanSynced() {
            EventHistory event = buildEvent("PRODUCTION_PLAN_SYNCED", "PP1001", null, null, null);
            String sentence = generator.generate(event);
            assertThat(sentence).contains("PP1001").contains("synchronized successfully");
        }

        @Test
        @DisplayName("PRODUCTION_PLAN_CREATED should include actor from createdBy")
        void productionPlanCreatedWithCreatedBy() {
            EventHistory event = buildEvent("PRODUCTION_PLAN_CREATED", "PP2001", null, null, null);
            event.setCreatedBy("John Doe");
            String sentence = generator.generate(event);
            assertThat(sentence).contains("PP2001").contains("John Doe");
        }

        @Test
        @DisplayName("PRODUCTION_PLAN_COMPLETED should generate correct sentence")
        void productionPlanCompleted() {
            EventHistory event = buildEvent("PRODUCTION_PLAN_COMPLETED", "PP3001", null, null, null);
            String sentence = generator.generate(event);
            assertThat(sentence).contains("PP3001").contains("completed");
        }
    }

    // =========================================================================
    // ASN MODULE
    // =========================================================================

    @Nested
    @DisplayName("ASN Module Events")
    class AsnModuleTests {

        @Test
        @DisplayName("ASN_APPROVED should use actor name from createdBy")
        void asnApproved() {
            EventHistory event = buildEvent("ASN_APPROVED", "ASN450", null, null, null);
            event.setCreatedBy("Rahul");
            String sentence = generator.generate(event);
            assertThat(sentence).contains("ASN450").contains("approved").contains("Rahul");
        }

        @Test
        @DisplayName("ASN_DISPATCH_STARTED should include warehouse from metadata")
        void asnDispatchStarted() {
            EventHistory event = buildEvent("ASN_DISPATCH_STARTED", "ASN450", null, null,
                    Map.of("warehouse", "Nagpur Warehouse"));
            String sentence = generator.generate(event);
            assertThat(sentence).contains("ASN450").contains("Nagpur Warehouse");
        }

        @Test
        @DisplayName("ASN_REJECTED should include rejection reason from metadata")
        void asnRejected() {
            EventHistory event = buildEvent("ASN_REJECTED", "ASN500", null, null,
                    Map.of("reason", "quantity mismatch"));
            String sentence = generator.generate(event);
            assertThat(sentence)
                    .contains("ASN500")
                    .contains("rejected")
                    .contains("quantity mismatch");
        }
    }

    // =========================================================================
    // MATERIAL MODULE
    // =========================================================================

    @Nested
    @DisplayName("Material Module Events")
    class MaterialModuleTests {

        @Test
        @DisplayName("MATERIAL_REQUEST_CREATED should reference production plan from metadata")
        void materialRequestCreated() {
            EventHistory event = buildEvent("MATERIAL_REQUEST_CREATED", "MR2001", null, null,
                    Map.of("productionPlanId", "PP1001"));
            String sentence = generator.generate(event);
            assertThat(sentence).contains("MR2001").contains("PP1001");
        }

        @Test
        @DisplayName("MATERIAL_RECEIVED should include warehouse from metadata")
        void materialReceived() {
            EventHistory event = buildEvent("MATERIAL_RECEIVED", "MR3001", null, null,
                    Map.of("productionPlanId", "PP2001", "warehouse", "Mumbai Store"));
            String sentence = generator.generate(event);
            assertThat(sentence).contains("PP2001").contains("Mumbai Store");
        }
    }

    // =========================================================================
    // FALLBACK
    // =========================================================================

    @Test
    @DisplayName("Unknown event type should produce generic fallback sentence")
    void unknownEventTypeFallback() {
        EventHistory event = buildEvent("SOME_UNKNOWN_EVENT", "REF123", "PURCHASE_ORDER",
                null, null);
        event.setStatus("PENDING");
        String sentence = generator.generate(event);
        assertThat(sentence)
                .contains("SOME_UNKNOWN_EVENT")
                .contains("REF123")
                .contains("PENDING");
    }

    @Test
    @DisplayName("Event with null referenceId produces fallback in sentence")
    void nullReferenceId() {
        EventHistory event = buildEvent("PRODUCTION_PLAN_SYNCED", null, null, null, null);
        String sentence = generator.generate(event);
        assertThat(sentence)
                .contains("synchronized")
                .doesNotContain("null");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private EventHistory buildEvent(String eventType, String referenceId,
                                    String referenceType, String moduleName,
                                    Map<String, Object> metadata) {
        return EventHistory.builder()
                .id(1L)
                .eventType(eventType)
                .moduleName(moduleName != null ? moduleName : "PRODUCTION")
                .referenceType(referenceType)
                .referenceId(referenceId)
                .status("SUCCESS")
                .eventTime(LocalDateTime.now())
                .metadata(metadata)
                .build();
    }
}
