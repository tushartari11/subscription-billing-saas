package com.rekreation.saas.subscriptionmanagement.dto.response;

import com.rekreation.saas.subscriptionmanagement.domain.Seller;
import com.rekreation.saas.subscriptionmanagement.domain.Seller.Role;
import com.rekreation.saas.subscriptionmanagement.domain.Seller.SellerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerResponse {

    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String businessName;
    private SellerStatus status;
    private Role role;
    private Instant createdAt;

    public static SellerResponse fromEntity(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .phoneNumber(seller.getPhoneNumber())
                .businessName(seller.getBusinessName())
                .status(seller.getStatus())
                .role(seller.getRole())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}