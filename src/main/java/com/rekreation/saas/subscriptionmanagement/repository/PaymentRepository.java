package com.rekreation.saas.subscriptionmanagement.repository;

import com.rekreation.saas.subscriptionmanagement.domain.Payment;
import com.rekreation.saas.subscriptionmanagement.domain.Payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByBillingId(UUID billingId);

    Optional<Payment> findByPaymentReference(String paymentReference);

    @Query("SELECT p FROM Payment p WHERE p.billing.subscriber.id = :subscriberId")
    Page<Payment> findBySubscriberId(@Param("subscriberId") UUID subscriberId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.billing.subscriber.seller.id = :sellerId")
    Page<Payment> findBySellerId(@Param("sellerId") UUID sellerId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.billing.subscriber.seller.id = :sellerId " +
           "AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    List<Payment> findBySellerIdAndDateRange(
            @Param("sellerId") UUID sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.billing.subscriber.seller.id = :sellerId " +
           "AND p.status = :status AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    BigDecimal sumAmountBySellerIdAndStatusAndDateRange(
            @Param("sellerId") UUID sellerId,
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}