package com.rekreation.saas.subscriptionmanagement.dto.response;

import com.rekreation.saas.subscriptionmanagement.domain.Payment;
import com.rekreation.saas.subscriptionmanagement.domain.Payment.PaymentMethod;
import com.rekreation.saas.subscriptionmanagement.domain.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private String paymentReference;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String notes;
    private UUID billingId;
    private String billNumber;
    private Instant createdAt;

    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .notes(payment.getNotes())
                .billingId(payment.getBilling().getId())
                .billNumber(payment.getBilling().getBillNumber())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}