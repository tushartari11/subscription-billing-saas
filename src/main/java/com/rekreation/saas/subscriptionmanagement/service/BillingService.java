package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Billing;
import com.rekreation.saas.subscriptionmanagement.domain.Billing.BillingStatus;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.SubscriptionStatus;
import com.rekreation.saas.subscriptionmanagement.dto.response.BillingResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.exception.ResourceNotFoundException;
import com.rekreation.saas.subscriptionmanagement.repository.BillingRepository;
import com.rekreation.saas.subscriptionmanagement.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingService {

    private final BillingRepository billingRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PageResponse<BillingResponse> findBySellerId(UUID sellerId, Pageable pageable) {
        Page<Billing> billings = billingRepository.findBySellerId(sellerId, pageable);
        return PageResponse.from(billings, BillingResponse::fromEntity);
    }

    public PageResponse<BillingResponse> findBySubscriberId(UUID subscriberId, Pageable pageable) {
        Page<Billing> billings = billingRepository.findBySubscriberId(subscriberId, pageable);
        return PageResponse.from(billings, BillingResponse::fromEntity);
    }

    public BillingResponse findById(UUID id) {
        Billing billing = findEntityById(id);
        return BillingResponse.fromEntity(billing);
    }

    public BillingResponse findByBillNumber(String billNumber) {
        Billing billing = billingRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "billNumber", billNumber));
        return BillingResponse.fromEntity(billing);
    }

    @Transactional
    public List<BillingResponse> generateMonthlyBills(UUID sellerId, YearMonth billingMonth) {
        List<Subscription> activeSubscriptions = subscriptionRepository
                .findBySellerIdAndStatus(sellerId, SubscriptionStatus.ACTIVE);

        LocalDate periodStart = billingMonth.atDay(1);
        LocalDate periodEnd = billingMonth.atEndOfMonth();
        LocalDate dueDate = periodEnd.plusDays(15);

        List<Billing> generatedBills = new ArrayList<>();

        for (Subscription subscription : activeSubscriptions) {
            if (subscription.getStartDate().isAfter(periodEnd)) {
                continue;
            }

            String billNumber = generateBillNumber(subscription, billingMonth);

            if (billingRepository.findByBillNumber(billNumber).isPresent()) {
                continue;
            }

            Billing billing = Billing.builder()
                    .billNumber(billNumber)
                    .billingPeriodStart(periodStart)
                    .billingPeriodEnd(periodEnd)
                    .totalAmount(subscription.getAmount())
                    .dueDate(dueDate)
                    .subscriber(subscription.getSubscriber())
                    .subscription(subscription)
                    .build();

            generatedBills.add(billingRepository.save(billing));
        }

        return generatedBills.stream()
                .map(BillingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void updateBillingStatus(UUID billingId) {
        Billing billing = findEntityById(billingId);
        updateBillingStatusInternal(billing);
        billingRepository.save(billing);
    }

    @Transactional
    public void markOverdueBillings() {
        List<Billing> overdueBillings = billingRepository.findOverdueBillings(LocalDate.now());
        for (Billing billing : overdueBillings) {
            billing.setStatus(BillingStatus.OVERDUE);
        }
        billingRepository.saveAll(overdueBillings);
    }

    public List<BillingResponse> findOverdueBillings(UUID sellerId) {
        return billingRepository.findBySellerIdAndStatus(sellerId, BillingStatus.OVERDUE).stream()
                .map(BillingResponse::fromEntity)
                .toList();
    }

    public Billing findEntityById(UUID id) {
        return billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", "id", id));
    }

    void updateBillingStatusInternal(Billing billing) {
        BigDecimal outstanding = billing.getOutstandingAmount();

        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            billing.setStatus(BillingStatus.PAID);
        } else if (billing.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            billing.setStatus(BillingStatus.PARTIAL);
        } else if (billing.getDueDate().isBefore(LocalDate.now())) {
            billing.setStatus(BillingStatus.OVERDUE);
        }
    }

    private String generateBillNumber(Subscription subscription, YearMonth billingMonth) {
        return String.format("BILL-%s-%s-%s",
                subscription.getSubscriber().getId().toString().substring(0, 8).toUpperCase(),
                subscription.getId().toString().substring(0, 4).toUpperCase(),
                billingMonth.toString().replace("-", ""));
    }
}