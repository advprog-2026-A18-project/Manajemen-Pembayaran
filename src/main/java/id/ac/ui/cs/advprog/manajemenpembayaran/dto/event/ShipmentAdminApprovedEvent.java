package id.ac.ui.cs.advprog.manajemenpembayaran.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentAdminApprovedEvent {
    private String eventId;
    private String mandorId;
    private BigDecimal kilogramDiakui;
    private LocalDateTime approvedAt;
}
