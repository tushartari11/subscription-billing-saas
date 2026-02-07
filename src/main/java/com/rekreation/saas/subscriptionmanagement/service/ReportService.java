package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Billing.BillingStatus;
import com.rekreation.saas.subscriptionmanagement.domain.Payment.PaymentStatus;
import com.rekreation.saas.subscriptionmanagement.domain.Subscriber.SubscriberStatus;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.SubscriptionStatus;
import com.rekreation.saas.subscriptionmanagement.dto.response.DashboardResponse;
import com.rekreation.saas.subscriptionmanagement.repository.BillingRepository;
import com.rekreation.saas.subscriptionmanagement.repository.PaymentRepository;
import com.rekreation.saas.subscriptionmanagement.repository.SubscriberRepository;
import com.rekreation.saas.subscriptionmanagement.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final SubscriberRepository subscriberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BillingRepository billingRepository;
    private final PaymentRepository paymentRepository;

    public DashboardResponse getDashboard(UUID sellerId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        LocalDateTime monthStartTime = monthStart.atStartOfDay();
        LocalDateTime monthEndTime = monthEnd.atTime(23, 59, 59);

        long totalSubscribers = subscriberRepository.findBySellerId(sellerId,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long activeSubscribers = subscriberRepository.countBySellerIdAndStatus(sellerId, SubscriberStatus.ACTIVE);
        long inactiveSubscribers = subscriberRepository.countBySellerIdAndStatus(sellerId, SubscriberStatus.INACTIVE);

        var allSubscriptions = subscriptionRepository.findBySellerId(sellerId);
        long totalSubscriptions = allSubscriptions.size();
        long activeSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .count();

        BigDecimal totalBilled = billingRepository.sumTotalAmountBySellerIdAndPeriod(sellerId, monthStart, monthEnd);
        BigDecimal totalCollected = paymentRepository.sumAmountBySellerIdAndStatusAndDateRange(
                sellerId, PaymentStatus.COMPLETED, monthStartTime, monthEndTime);

        if (totalBilled == null) totalBilled = BigDecimal.ZERO;
        if (totalCollected == null) totalCollected = BigDecimal.ZERO;

        BigDecimal outstanding = totalBilled.subtract(totalCollected);

        var unpaidBillings = billingRepository.findBySellerIdAndStatus(sellerId, BillingStatus.UNPAID);
        var overdueBillings = billingRepository.findBySellerIdAndStatus(sellerId, BillingStatus.OVERDUE);

        double collectionEfficiency = 0.0;
        if (totalBilled.compareTo(BigDecimal.ZERO) > 0) {
            collectionEfficiency = totalCollected
                    .divide(totalBilled, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return DashboardResponse.builder()
                .totalSubscribers(totalSubscribers)
                .activeSubscribers(activeSubscribers)
                .inactiveSubscribers(inactiveSubscribers)
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .totalBilledAmount(totalBilled)
                .totalCollectedAmount(totalCollected)
                .totalOutstandingAmount(outstanding)
                .unpaidBills(unpaidBillings.size())
                .overdueBills(overdueBillings.size())
                .collectionEfficiency(collectionEfficiency)
                .build();
    }
}