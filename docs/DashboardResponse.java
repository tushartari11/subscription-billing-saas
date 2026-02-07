package com.billing.saas.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private Integer totalSubscribers;
    private Integer activeSubscribers;
    private Integer inactiveSubscribers;
    
    private BigDecimal totalOutstanding;
    private BigDecimal collectedThisMonth;
    private BigDecimal pendingThisMonth;
    
    private Integer paidBillsCount;
    private Integer unpaidBillsCount;
    private Integer partiallyPaidBillsCount;
    
    private Double paymentCollectionRate; // Percentage
    
    private RevenueStats revenueStats;
    private List<PaymentMethodBreakdown> paymentBreakdown;
}