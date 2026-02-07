package com.rekreation.saas.subscriptionmanagement.dto.response;

import com.rekreation.saas.subscriptionmanagement.domain.Subscription;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.BillingCycle;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.SubscriptionStatus;
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
public class SubscriptionResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal amount;
    private BillingCycle billingCycle;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private UUID subscriberId;
    private Instant createdAt;

    public static SubscriptionResponse fromEntity(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .name(subscription.getName())
                .description(subscription.getDescription())
                .amount(subscription.getAmount())
                .billingCycle(subscription.getBillingCycle())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .subscriberId(subscription.getSubscriber().getId())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}