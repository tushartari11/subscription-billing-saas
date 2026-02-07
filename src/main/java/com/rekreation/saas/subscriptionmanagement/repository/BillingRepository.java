package com.rekreation.saas.subscriptionmanagement.repository;

import com.rekreation.saas.subscriptionmanagement.domain.Billing;
import com.rekreation.saas.subscriptionmanagement.domain.Billing.BillingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingRepository extends JpaRepository<Billing, UUID> {

    Page<Billing> findBySubscriberId(UUID subscriberId, Pageable pageable);

    List<Billing> findBySubscriberIdAndStatus(UUID subscriberId, BillingStatus status);

    Optional<Billing> findByBillNumber(String billNumber);

    @Query("SELECT b FROM Billing b WHERE b.subscriber.seller.id = :sellerId")
    Page<Billing> findBySellerId(@Param("sellerId") UUID sellerId, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.subscriber.seller.id = :sellerId AND b.status = :status")
    List<Billing> findBySellerIdAndStatus(
            @Param("sellerId") UUID sellerId,
            @Param("status") BillingStatus status);

    @Query("SELECT b FROM Billing b WHERE b.dueDate < :date AND b.status IN ('UNPAID', 'PARTIAL')")
    List<Billing> findOverdueBillings(@Param("date") LocalDate date);

    @Query("SELECT SUM(b.totalAmount) FROM Billing b WHERE b.subscriber.seller.id = :sellerId " +
           "AND b.billingPeriodStart >= :startDate AND b.billingPeriodEnd <= :endDate")
    BigDecimal sumTotalAmountBySellerIdAndPeriod(
            @Param("sellerId") UUID sellerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(b.paidAmount) FROM Billing b WHERE b.subscriber.seller.id = :sellerId " +
           "AND b.billingPeriodStart >= :startDate AND b.billingPeriodEnd <= :endDate")
    BigDecimal sumPaidAmountBySellerIdAndPeriod(
            @Param("sellerId") UUID sellerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}