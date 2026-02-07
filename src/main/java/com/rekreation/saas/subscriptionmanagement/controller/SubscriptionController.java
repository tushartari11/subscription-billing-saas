package com.rekreation.saas.subscriptionmanagement.controller;

import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriptionCreateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.SubscriptionResponse;
import com.rekreation.saas.subscriptionmanagement.security.CurrentSeller;
import com.rekreation.saas.subscriptionmanagement.security.SellerPrincipal;
import com.rekreation.saas.subscriptionmanagement.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "List all subscriptions", description = "Get all subscriptions for the current seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<SubscriptionResponse>> getAll(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller) {
        List<SubscriptionResponse> response = subscriptionService.findBySellerId(seller.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriber/{subscriberId}")
    @Operation(summary = "Get subscriptions by subscriber", description = "Get all subscriptions for a specific subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<SubscriptionResponse>> getBySubscriber(
            @Parameter(description = "Subscriber ID") @PathVariable UUID subscriberId) {
        List<SubscriptionResponse> response = subscriptionService.findBySubscriberId(subscriberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID", description = "Retrieve a specific subscription by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionResponse> getById(
            @Parameter(description = "Subscription ID") @PathVariable UUID id) {
        SubscriptionResponse response = subscriptionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create subscription", description = "Create a new subscription for a subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionResponse> create(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Valid @RequestBody SubscriptionCreateRequest request) {
        SubscriptionResponse response = subscriptionService.create(request, seller.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel an active subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription cancelled successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionResponse> cancel(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscription ID") @PathVariable UUID id) {
        SubscriptionResponse response = subscriptionService.cancel(id, seller.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pause")
    @Operation(summary = "Pause subscription", description = "Pause an active subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription paused successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionResponse> pause(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscription ID") @PathVariable UUID id) {
        SubscriptionResponse response = subscriptionService.pause(id, seller.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resume")
    @Operation(summary = "Resume subscription", description = "Resume a paused subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription resumed successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriptionResponse> resume(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscription ID") @PathVariable UUID id) {
        SubscriptionResponse response = subscriptionService.resume(id, seller.getId());
        return ResponseEntity.ok(response);
    }
}