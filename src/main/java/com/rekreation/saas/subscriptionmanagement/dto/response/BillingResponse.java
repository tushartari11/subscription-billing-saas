package com.rekreation.saas.subscriptionmanagement.dto.response;

import com.rekreation.saas.subscriptionmanagement.domain.Billing;
import com.rekreation.saas.subscriptionmanagement.domain.Billing.BillingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingResponse {

    private UUID id;
    private String billNumber;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private BillingStatus status;
    private String notes;
    private UUID subscriberId;
    private String subscriberName;
    private UUID subscriptionId;
    private String subscriptionName;
    private Instant createdAt;

    public static BillingResponse fromEntity(Billing billing) {
        return BillingResponse.builder()
                .id(billing.getId())
                .billNumber(billing.getBillNumber())
                .billingPeriodStart(billing.getBillingPeriodStart())
                .billingPeriodEnd(billing.getBillingPeriodEnd())
                .totalAmount(billing.getTotalAmount())
                .paidAmount(billing.getPaidAmount())
                .outstandingAmount(billing.getOutstandingAmount())
                .dueDate(billing.getDueDate())
                .status(billing.getStatus())
                .notes(billing.getNotes())
                .subscriberId(billing.getSubscriber().getId())
                .subscriberName(billing.getSubscriber().getName())
                .subscriptionId(billing.getSubscription().getId())
                .subscriptionName(billing.getSubscription().getName())
                .createdAt(billing.getCreatedAt())
                .build();
    }
}