package com.rekreation.saas.subscriptionmanagement.repository;

import com.rekreation.saas.subscriptionmanagement.domain.Subscriber;
import com.rekreation.saas.subscriptionmanagement.domain.Subscriber.SubscriberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {

    Page<Subscriber> findBySellerId(UUID sellerId, Pageable pageable);

    List<Subscriber> findBySellerIdAndStatus(UUID sellerId, SubscriberStatus status);

    Optional<Subscriber> findByIdAndSellerId(UUID id, UUID sellerId);

    @Query("SELECT s FROM Subscriber s WHERE s.seller.id = :sellerId " +
           "AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR s.phoneNumber LIKE CONCAT('%', :search, '%'))")
    Page<Subscriber> searchBySellerIdAndKeyword(
            @Param("sellerId") UUID sellerId,
            @Param("search") String search,
            Pageable pageable);

    long countBySellerIdAndStatus(UUID sellerId, SubscriberStatus status);
}