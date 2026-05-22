package id.ac.ui.cs.advprog.manajemenpembayaran.grpc;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollEventProcessResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.PayrollProcessResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessHarvestApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessShipmentAdminApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessShipmentMandorApprovedRequest;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentInternalGrpcServiceTest {
    @Mock
    private PayrollEventService payrollEventService;

    @Mock
    private StreamObserver<PayrollProcessResponse> observer;

    private PaymentInternalGrpcService grpcService;

    @BeforeEach
    void setUp() {
        grpcService = new PaymentInternalGrpcService(payrollEventService);
    }

    @Test
    void processHarvestApprovedMapsRequestAndReturnsCreatedResponse() {
        Payroll payroll = payroll(11L, PayrollSourceType.HARVEST_APPROVAL, "180000.00");
        when(payrollEventService.processHarvestApprovedWithResult(any(HarvestApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(true).build());

        grpcService.processHarvestApproved(ProcessHarvestApprovedRequest.newBuilder()
                .setEventId("panen-1")
                .setBuruhId("buruh-1")
                .setKilogram(100.0)
                .build(), observer);

        ArgumentCaptor<HarvestApprovedEvent> eventCaptor = ArgumentCaptor.forClass(HarvestApprovedEvent.class);
        verify(payrollEventService).processHarvestApprovedWithResult(eventCaptor.capture());
        assertEquals("panen-1", eventCaptor.getValue().getEventId());
        assertEquals("buruh-1", eventCaptor.getValue().getBuruhId());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(eventCaptor.getValue().getKilogram()));
        verify(observer).onNext(argThat(response -> response.getSuccess()
                && response.getPayrollId() == 11L
                && !response.getDuplicate()
                && response.getSourceType().equals("HARVEST_APPROVAL")));
        verify(observer).onCompleted();
    }

    @Test
    void processShipmentMandorApprovedMapsRequestAndReturnsDuplicateResponse() {
        Payroll payroll = payroll(12L, PayrollSourceType.SHIPMENT_MANDOR_APPROVAL, "67500.00");
        when(payrollEventService.processShipmentMandorApprovedWithResult(any(ShipmentMandorApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(false).build());

        grpcService.processShipmentMandorApproved(ProcessShipmentMandorApprovedRequest.newBuilder()
                .setEventId("shipment-1")
                .setSupirId("supir-1")
                .setKilogram(50.0)
                .build(), observer);

        ArgumentCaptor<ShipmentMandorApprovedEvent> eventCaptor = ArgumentCaptor.forClass(ShipmentMandorApprovedEvent.class);
        verify(payrollEventService).processShipmentMandorApprovedWithResult(eventCaptor.capture());
        assertEquals("shipment-1", eventCaptor.getValue().getEventId());
        assertEquals("supir-1", eventCaptor.getValue().getSupirId());
        verify(observer).onNext(argThat(response -> response.getSuccess()
                && response.getPayrollId() == 12L
                && response.getDuplicate()
                && response.getMessage().equals("Duplicate event ignored")));
        verify(observer).onCompleted();
    }

    @Test
    void processShipmentAdminApprovedMapsRequestAndReturnsCreatedResponse() {
        Payroll payroll = payroll(13L, PayrollSourceType.SHIPMENT_ADMIN_APPROVAL, "180000.00");
        when(payrollEventService.processShipmentAdminApprovedWithResult(any(ShipmentAdminApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(true).build());

        grpcService.processShipmentAdminApproved(ProcessShipmentAdminApprovedRequest.newBuilder()
                .setEventId("shipment-2")
                .setMandorId("mandor-1")
                .setKilogramDiakui(80.0)
                .build(), observer);

        ArgumentCaptor<ShipmentAdminApprovedEvent> eventCaptor = ArgumentCaptor.forClass(ShipmentAdminApprovedEvent.class);
        verify(payrollEventService).processShipmentAdminApprovedWithResult(eventCaptor.capture());
        assertEquals("shipment-2", eventCaptor.getValue().getEventId());
        assertEquals("mandor-1", eventCaptor.getValue().getMandorId());
        verify(observer).onNext(argThat(response -> response.getSuccess()
                && response.getPayrollId() == 13L
                && !response.getDuplicate()
                && response.getAmount() == 180000.0));
        verify(observer).onCompleted();
    }

    @Test
    void processHarvestApprovedReturnsInvalidArgumentWhenServiceThrows() {
        when(payrollEventService.processHarvestApprovedWithResult(any(HarvestApprovedEvent.class)))
                .thenThrow(new IllegalArgumentException("eventId is required"));

        grpcService.processHarvestApproved(ProcessHarvestApprovedRequest.newBuilder().build(), observer);

        verify(observer).onError(argThat(error -> error instanceof StatusRuntimeException
                && ((StatusRuntimeException) error).getStatus().getDescription().equals("eventId is required")));
        verify(observer, never()).onCompleted();
    }

    @Test
    void processShipmentAdminApprovedReturnsDefaultResponseValuesWhenPayrollFieldsAreNull() {
        Payroll payroll = payroll(null, null, null);
        when(payrollEventService.processShipmentAdminApprovedWithResult(any(ShipmentAdminApprovedEvent.class)))
                .thenReturn(PayrollEventProcessResult.builder().payroll(payroll).created(true).build());

        grpcService.processShipmentAdminApproved(ProcessShipmentAdminApprovedRequest.newBuilder()
                .setEventId("shipment-null-fields")
                .setMandorId("mandor-1")
                .setKilogramDiakui(80.0)
                .build(), observer);

        verify(observer).onNext(argThat(response -> response.getSuccess()
                && response.getPayrollId() == 0L
                && response.getAmount() == 0.0
                && response.getSourceType().isEmpty()));
        verify(observer).onCompleted();
    }

    @Test
    void processShipmentMandorApprovedReturnsInvalidArgumentWhenServiceThrows() {
        when(payrollEventService.processShipmentMandorApprovedWithResult(any(ShipmentMandorApprovedEvent.class)))
                .thenThrow(new IllegalArgumentException("supirId is required"));

        grpcService.processShipmentMandorApproved(ProcessShipmentMandorApprovedRequest.newBuilder().build(), observer);

        verify(observer).onError(argThat(error -> error instanceof StatusRuntimeException
                && ((StatusRuntimeException) error).getStatus().getDescription().equals("supirId is required")));
        verify(observer, never()).onCompleted();
    }

    private Payroll payroll(Long id, PayrollSourceType sourceType, String amount) {
        return Payroll.builder()
                .id(id)
                .amount(amount == null ? null : new BigDecimal(amount))
                .sourceType(sourceType)
                .build();
    }
}
