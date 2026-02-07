package com.rekreation.saas.subscriptionmanagement.dto.response;

import com.rekreation.saas.subscriptionmanagement.domain.Subscriber;
import com.rekreation.saas.subscriptionmanagement.domain.Subscriber.SubscriberStatus;
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
public class SubscriberResponse {

    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String whatsappNumber;
    private String address;
    private SubscriberStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static SubscriberResponse fromEntity(Subscriber subscriber) {
        return SubscriberResponse.builder()
                .id(subscriber.getId())
                .name(subscriber.getName())
                .email(subscriber.getEmail())
                .phoneNumber(subscriber.getPhoneNumber())
                .whatsappNumber(subscriber.getWhatsappNumber())
                .address(subscriber.getAddress())
                .status(subscriber.getStatus())
                .createdAt(subscriber.getCreatedAt())
                .updatedAt(subscriber.getUpdatedAt())
                .build();
    }
}