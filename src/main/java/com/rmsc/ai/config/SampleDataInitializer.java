package com.rmsc.ai.config;

import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.repository.EventHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Startup database initializer to insert sample manufacturing event logs
 * representing the full event lifecycle trace for testing.
 */
@Component
public class SampleDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final EventHistoryRepository eventHistoryRepository;

    public SampleDataInitializer(EventHistoryRepository eventHistoryRepository) {
        this.eventHistoryRepository = eventHistoryRepository;
    }

    @Override
    public void run(String... args) {
        if (!eventHistoryRepository.findTimelineByEventReferenceId("EVTMFG001").isEmpty()) {
            log.info("Database already contains 'evt-mfg-001' sample events, skipping startup initialization.");
            return;
        }

        log.info("Initializing database with complete manufacturing sample event trace 'evt-mfg-001'...");

        String correlationId = "evt-mfg-001";
        LocalDateTime now = LocalDateTime.now();

        EventHistory planUploaded = EventHistory.builder()
                .eventType("PRODUCTION_PLAN_UPLOADED")
                .moduleName("PRODUCTION")
                .referenceType("PRODUCTION_PLAN")
                .referenceId("PP-2026-001")
                .eventReferenceId(correlationId)
                .description("PPE Manager uploaded Production Plan PP-2026-001 and assigned it to PPE Officer Rahul.")
                .status("SUCCESS")
                .eventTime(now.minusHours(8))
                .metadata(Map.of("productionPlanId", "PP-2026-001", "assignedTo", "Rahul"))
                .createdBy("PPE Manager")
                .build();

        EventHistory planCommitted = EventHistory.builder()
                .eventType("PRODUCTION_PLAN_COMMITTED")
                .moduleName("PRODUCTION")
                .referenceType("PRODUCTION_PLAN")
                .referenceId("PP-2026-001")
                .eventReferenceId(correlationId)
                .description("PPE Officer committed Plan PP-2026-001 due to material shortage of steel rods.")
                .status("SHORTAGE")
                .eventTime(now.minusHours(7))
                .metadata(Map.of("productionPlanId", "PP-2026-001", "shortageItems", List.of("STEEL_ROD_10MM")))
                .createdBy("Rahul")
                .build();

        EventHistory procurementReq = EventHistory.builder()
                .eventType("PROCUREMENT_REQUEST_CREATED")
                .moduleName("MATERIAL")
                .referenceType("MATERIAL_REQUEST")
                .referenceId("MR-6001")
                .eventReferenceId(correlationId)
                .description("Procurement request MR-6001 for steel rods was generated and sent to Supplier Alpha.")
                .status("PENDING")
                .eventTime(now.minusHours(6))
                .metadata(Map.of("productionPlanId", "PP-2026-001", "materialRequestId", "MR-6001", "supplier", "Alpha"))
                .createdBy("System")
                .build();

        EventHistory asnDispatched = EventHistory.builder()
                .eventType("ASN_DISPATCHED")
                .moduleName("ASN")
                .referenceType("ASN")
                .referenceId("ASN-4501")
                .eventReferenceId(correlationId)
                .description("Supplier Alpha accepted request MR-6001 and dispatched ASN-4501.")
                .status("DISPATCHED")
                .eventTime(now.minusHours(5))
                .metadata(Map.of("productionPlanId", "PP-2026-001", "asnId", "ASN-4501", "carrier", "FreightFast"))
                .createdBy("Supplier Alpha")
                .build();

        EventHistory gateScan = EventHistory.builder()
                .eventType("GATESECURITY_SCAN")
                .moduleName("LOGISTICS")
                .referenceType("ASN")
                .referenceId("ASN-4501")
                .eventReferenceId(correlationId)
                .description("Gate Security scanned ASN-4501 and validated carrier details.")
                .status("VALIDATED")
                .eventTime(now.minusHours(4))
                .metadata(Map.of("asnId", "ASN-4501", "gateNumber", 2))
                .createdBy("Gate Guard")
                .build();

        EventHistory cinGenerated = EventHistory.builder()
                .eventType("CIN_GENERATED")
                .moduleName("WAREHOUSE")
                .referenceType("CIN")
                .referenceId("CIN-9002")
                .eventReferenceId(correlationId)
                .description("Material Clerk confirmed dispatch documents and created CIN-9002.")
                .status("APPROVED")
                .eventTime(now.minusHours(3))
                .metadata(Map.of("asnId", "ASN-4501", "cinId", "CIN-9002"))
                .createdBy("Clerk Mike")
                .build();

        EventHistory containerAccepted = EventHistory.builder()
                .eventType("CONTAINER_ACCEPTED")
                .moduleName("WAREHOUSE")
                .referenceType("CONTAINER")
                .referenceId("CONT-102")
                .eventReferenceId(correlationId)
                .description("Shipment container CONT-102 accepted at unloading dock.")
                .status("RECEIVED")
                .eventTime(now.minusHours(2))
                .metadata(Map.of("containerId", "CONT-102", "dockNumber", "Dock-B"))
                .createdBy("Dock Supervisor")
                .build();

        EventHistory qcRejected = EventHistory.builder()
                .eventType("QC_REJECTED")
                .moduleName("QUALITY")
                .referenceType("QC_REPORT")
                .referenceId("QC-8003")
                .eventReferenceId(correlationId)
                .description("QC rejected materials for plan PP-2026-001 due to micro-cracks on steel rods.")
                .status("REJECTED")
                .eventTime(now.minusHours(1))
                .metadata(Map.of("productionPlanId", "PP-2026-001", "reason", "micro-cracks"))
                .createdBy("QC Inspector")
                .build();

        eventHistoryRepository.saveAll(List.of(
                planUploaded, planCommitted, procurementReq, asnDispatched,
                gateScan, cinGenerated, containerAccepted, qcRejected
        ));

        log.info("Successfully loaded 8 manufacturing timeline events for 'evt-mfg-001' into the database!");
    }
}
