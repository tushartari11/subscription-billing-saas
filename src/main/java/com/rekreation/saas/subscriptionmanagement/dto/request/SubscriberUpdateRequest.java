package com.rekreation.saas.subscriptionmanagement.dto.request;

import com.rekreation.saas.subscriptionmanagement.domain.Subscriber.SubscriberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String whatsappNumber;

    private String address;

    private SubscriberStatus status;
}