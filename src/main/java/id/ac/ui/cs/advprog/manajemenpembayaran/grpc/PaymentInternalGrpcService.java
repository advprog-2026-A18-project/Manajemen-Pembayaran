package id.ac.ui.cs.advprog.manajemenpembayaran.grpc;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollEventProcessResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentAdminApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.ShipmentMandorApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollEventService;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.PaymentInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.PayrollProcessResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessHarvestApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessShipmentAdminApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.payment.ProcessShipmentMandorApprovedRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentInternalGrpcService extends PaymentInternalServiceGrpc.PaymentInternalServiceImplBase {
    private final PayrollEventService payrollEventService;

    public PaymentInternalGrpcService(PayrollEventService payrollEventService) {
        this.payrollEventService = payrollEventService;
    }

    @Override
    public void processHarvestApproved(ProcessHarvestApprovedRequest request,
                                       StreamObserver<PayrollProcessResponse> responseObserver) {
        try {
            HarvestApprovedEvent event = new HarvestApprovedEvent();
            event.setEventId(request.getEventId());
            event.setBuruhId(request.getBuruhId());
            event.setKilogram(BigDecimal.valueOf(request.getKilogram()));
            event.setApprovedAt(LocalDateTime.now());
            complete(payrollEventService.processHarvestApprovedWithResult(event), responseObserver);
        } catch (RuntimeException exception) {
            fail(exception, responseObserver);
        }
    }

    @Override
    public void processShipmentMandorApproved(ProcessShipmentMandorApprovedRequest request,
                                              StreamObserver<PayrollProcessResponse> responseObserver) {
        try {
            ShipmentMandorApprovedEvent event = new ShipmentMandorApprovedEvent();
            event.setEventId(request.getEventId());
            event.setSupirId(request.getSupirId());
            event.setKilogram(BigDecimal.valueOf(request.getKilogram()));
            event.setApprovedAt(LocalDateTime.now());
            complete(payrollEventService.processShipmentMandorApprovedWithResult(event), responseObserver);
        } catch (RuntimeException exception) {
            fail(exception, responseObserver);
        }
    }

    @Override
    public void processShipmentAdminApproved(ProcessShipmentAdminApprovedRequest request,
                                             StreamObserver<PayrollProcessResponse> responseObserver) {
        try {
            ShipmentAdminApprovedEvent event = new ShipmentAdminApprovedEvent();
            event.setEventId(request.getEventId());
            event.setMandorId(request.getMandorId());
            event.setKilogramDiakui(BigDecimal.valueOf(request.getKilogramDiakui()));
            event.setApprovedAt(LocalDateTime.now());
            complete(payrollEventService.processShipmentAdminApprovedWithResult(event), responseObserver);
        } catch (RuntimeException exception) {
            fail(exception, responseObserver);
        }
    }

    private void complete(PayrollEventProcessResult result, StreamObserver<PayrollProcessResponse> responseObserver) {
        Payroll payroll = result.getPayroll();
        responseObserver.onNext(PayrollProcessResponse.newBuilder()
                .setSuccess(true)
                .setPayrollId(payroll.getId() == null ? 0L : payroll.getId())
                .setDuplicate(!result.isCreated())
                .setAmount(payroll.getAmount() == null ? 0.0 : payroll.getAmount().doubleValue())
                .setSourceType(payroll.getSourceType() == null ? "" : payroll.getSourceType().name())
                .setMessage(result.isCreated() ? "Payroll created" : "Duplicate event ignored")
                .build());
        responseObserver.onCompleted();
    }

    private void fail(RuntimeException exception, StreamObserver<PayrollProcessResponse> responseObserver) {
        responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(exception.getMessage())
                .withCause(exception)
                .asRuntimeException());
    }
}
