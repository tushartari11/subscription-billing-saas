package com.billing.saas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
public class SubscriberCreateRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "WhatsApp must be 10 digits")
    private String whatsappNumber;
    
    @Email
    private String email;
    
    @Size(max = 500)
    private String address;
    
    @NotNull(message = "Subscription plan is required")
    private Long subscriptionPlanId;
    
    @NotNull
    @Min(1)
    private Integer quantityPerDay;
    
    @NotNull
    private LocalDate subscriptionStartDate;
}