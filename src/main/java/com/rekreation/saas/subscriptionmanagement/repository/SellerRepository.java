package com.rekreation.saas.subscriptionmanagement.repository;

import com.rekreation.saas.subscriptionmanagement.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {

    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);
}