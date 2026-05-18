package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionType;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    @Test
    void recordTopUpShouldSaveTopUpTransaction() {
        transactionHistoryService.recordTopUp("admin-default", BigDecimal.valueOf(250000), 6L);

        ArgumentCaptor<TransactionHistory> transactionCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(transactionCaptor.capture());
        TransactionHistory transaction = transactionCaptor.getValue();

        assertEquals("admin-default", transaction.getOwnerId());
        assertEquals(TransactionType.TOP_UP, transaction.getType());
        assertEquals(BigDecimal.valueOf(250000), transaction.getAmount());
        assertEquals("TOP_UP_REQUEST", transaction.getReferenceType());
        assertEquals("6", transaction.getReferenceId());
    }

    @Test
    void recordPayrollPaymentShouldSavePayrollTransaction() {
        transactionHistoryService.recordPayrollPayment("buruh-9", BigDecimal.valueOf(180000), 9L);

        ArgumentCaptor<TransactionHistory> transactionCaptor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(transactionCaptor.capture());
        TransactionHistory transaction = transactionCaptor.getValue();

        assertEquals("buruh-9", transaction.getOwnerId());
        assertEquals(TransactionType.PAYROLL_PAYMENT, transaction.getType());
        assertEquals(BigDecimal.valueOf(180000), transaction.getAmount());
        assertEquals("PAYROLL", transaction.getReferenceType());
        assertEquals("9", transaction.getReferenceId());
    }
}
