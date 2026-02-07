package com.rekreation.saas.subscriptionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalSubscribers;
    private long activeSubscribers;
    private long inactiveSubscribers;
    private long totalSubscriptions;
    private long activeSubscriptions;
    private BigDecimal totalBilledAmount;
    private BigDecimal totalCollectedAmount;
    private BigDecimal totalOutstandingAmount;
    private long unpaidBills;
    private long overdueBills;
    private double collectionEfficiency;
}