package com.rekreation.saas.subscriptionmanagement.repository;

import com.rekreation.saas.subscriptionmanagement.domain.Subscription;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findBySubscriberId(UUID subscriberId);

    List<Subscription> findBySubscriberIdAndStatus(UUID subscriberId, SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.subscriber.seller.id = :sellerId AND s.status = :status")
    List<Subscription> findBySellerIdAndStatus(
            @Param("sellerId") UUID sellerId,
            @Param("status") SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.subscriber.seller.id = :sellerId")
    List<Subscription> findBySellerId(@Param("sellerId") UUID sellerId);
}