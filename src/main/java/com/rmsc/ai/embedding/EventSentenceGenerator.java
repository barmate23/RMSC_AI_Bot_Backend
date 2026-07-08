package com.rmsc.ai.embedding;

import com.rmsc.ai.entity.EventHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Converts a raw {@link EventHistory} database record into a single,
 * meaningful business sentence for semantic embedding.
 */
@Component
public class EventSentenceGenerator {

    private static final Logger log = LoggerFactory.getLogger(EventSentenceGenerator.class);

    public String generate(EventHistory event) {
        Objects.requireNonNull(event, "event must not be null");

        String eventType = normalise(event.getEventType());
        String sentence = switch (eventType) {
            case "PRODUCTION_PLAN_SYNCED"     -> productionPlanSynced(event);
            case "PRODUCTION_PLAN_CREATED"    -> productionPlanCreated(event);
            case "PRODUCTION_PLAN_UPDATED"    -> productionPlanUpdated(event);
            case "PRODUCTION_PLAN_COMPLETED"  -> productionPlanCompleted(event);

            case "MATERIAL_REQUEST_CREATED"   -> materialRequestCreated(event);
            case "MATERIAL_REQUEST_APPROVED"  -> materialRequestApproved(event);
            case "MATERIAL_RECEIVED"          -> materialReceived(event);
            case "MATERIAL_DISPATCHED"        -> materialDispatched(event);

            case "ASN_CREATED"                -> asnCreated(event);
            case "ASN_APPROVED"               -> asnApproved(event);
            case "ASN_REJECTED"               -> asnRejected(event);
            case "ASN_DISPATCH_STARTED"       -> asnDispatchStarted(event);
            case "ASN_RECEIVED"               -> asnReceived(event);

            case "STOCK_ADJUSTED"             -> stockAdjusted(event);
            case "STOCK_TRANSFERRED"          -> stockTransferred(event);
            case "LOW_STOCK_ALERT"            -> lowStockAlert(event);

            default -> genericFallback(event);
        };

        log.debug("Generated sentence for event [id={}, type={}]: {}", event.getId(), event.getEventType(), sentence);
        return sentence;
    }

    private String productionPlanSynced(EventHistory e) {
        return String.format("Production Plan %s synchronized successfully by %s.",
                ref(e), actor(e, "Planning System"));
    }

    private String productionPlanCreated(EventHistory e) {
        return String.format("Production Plan %s was created by %s for %s.",
                ref(e), actor(e, "System"), module(e));
    }

    private String productionPlanUpdated(EventHistory e) {
        return String.format("Production Plan %s was updated by %s. Status: %s.",
                ref(e), actor(e, "System"), status(e));
    }

    private String productionPlanCompleted(EventHistory e) {
        return String.format("Production Plan %s was marked as completed by %s.",
                ref(e), actor(e, "System"));
    }

    private String materialRequestCreated(EventHistory e) {
        String plan = metaString(e, "productionPlanId", "unknown plan");
        return String.format("Material Request %s was generated for Production Plan %s.",
                ref(e), plan);
    }

    private String materialRequestApproved(EventHistory e) {
        return String.format("Material Request %s was approved by %s.",
                ref(e), actor(e, "Approver"));
    }

    private String materialReceived(EventHistory e) {
        String plan = metaString(e, "productionPlanId", "unknown plan");
        return String.format("Material received successfully for Production Plan %s at %s.",
                plan, metaString(e, "warehouse", "warehouse"));
    }

    private String materialDispatched(EventHistory e) {
        String asn = metaString(e, "asnId", ref(e));
        String warehouse = metaString(e, "warehouse", "warehouse");
        return String.format("Material dispatch for %s started from %s.", asn, warehouse);
    }

    private String asnCreated(EventHistory e) {
        return String.format("ASN %s was created by %s.", ref(e), actor(e, "System"));
    }

    private String asnApproved(EventHistory e) {
        return String.format("ASN %s was approved by %s.", ref(e), actor(e, "Store Manager"));
    }

    private String asnRejected(EventHistory e) {
        String reason = metaString(e, "reason", "unspecified reason");
        return String.format("ASN %s was rejected by %s due to %s.", ref(e), actor(e, "Store Manager"), reason);
    }

    private String asnDispatchStarted(EventHistory e) {
        String warehouse = metaString(e, "warehouse", "warehouse");
        return String.format("Material dispatch for %s started from %s.", ref(e), warehouse);
    }

    private String asnReceived(EventHistory e) {
        return String.format("ASN %s has been received at %s.",
                ref(e), metaString(e, "location", "destination"));
    }

    private String stockAdjusted(EventHistory e) {
        String qty = metaString(e, "quantity", "unknown quantity");
        String item = metaString(e, "itemCode", ref(e));
        return String.format("Stock for item %s was adjusted by %s units by %s.", item, qty, actor(e, "Warehouse Manager"));
    }

    private String stockTransferred(EventHistory e) {
        String from = metaString(e, "fromLocation", "source");
        String to   = metaString(e, "toLocation", "destination");
        return String.format("Stock for %s was transferred from %s to %s by %s.",
                ref(e), from, to, actor(e, "System"));
    }

    private String lowStockAlert(EventHistory e) {
        String threshold = metaString(e, "threshold", "minimum level");
        return String.format("Low stock alert triggered for item %s — stock fell below %s.",
                ref(e), threshold);
    }

    private String genericFallback(EventHistory e) {
        return String.format("%s event occurred for %s %s in the %s module. Status: %s.",
                e.getEventType(), notBlankOr(e.getReferenceType(), "entity"),
                notBlankOr(e.getReferenceId(), "unknown"),
                notBlankOr(e.getModuleName(), "ERP"),
                notBlankOr(e.getStatus(), "recorded"));
    }

    private String ref(EventHistory e) {
        return notBlankOr(e.getReferenceId(), "unknown");
    }

    private String module(EventHistory e) {
        return notBlankOr(e.getModuleName(), "the system");
    }

    private String status(EventHistory e) {
        return notBlankOr(e.getStatus(), "updated");
    }

    private String actor(EventHistory e, String defaultActor) {
        String fromMeta = metaString(e, "actorName", null);
        if (fromMeta != null) return fromMeta;
        return notBlankOr(e.getCreatedBy(), defaultActor);
    }

    private String metaString(EventHistory e, String key, String defaultValue) {
        if (e.getMetadata() == null) return defaultValue;
        Object value = e.getMetadata().get(key);
        return (value != null && !value.toString().isBlank()) ? value.toString() : defaultValue;
    }

    private String notBlankOr(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private String normalise(String eventType) {
        return eventType == null ? "" : eventType.trim().toUpperCase();
    }
}
