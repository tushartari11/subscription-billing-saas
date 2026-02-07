package com.rekreation.saas.subscriptionmanagement.controller;

import com.rekreation.saas.subscriptionmanagement.dto.request.PaymentRecordRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.PaymentResponse;
import com.rekreation.saas.subscriptionmanagement.security.CurrentSeller;
import com.rekreation.saas.subscriptionmanagement.security.SellerPrincipal;
import com.rekreation.saas.subscriptionmanagement.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "List all payments", description = "Get paginated list of payments for the current seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PageResponse<PaymentResponse>> getAll(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<PaymentResponse> response = paymentService.findBySellerId(seller.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriber/{subscriberId}")
    @Operation(summary = "Get payments by subscriber", description = "Get paginated payments for a specific subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PageResponse<PaymentResponse>> getBySubscriber(
            @Parameter(description = "Subscriber ID") @PathVariable UUID subscriberId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<PaymentResponse> response = paymentService.findBySubscriberId(subscriberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/billing/{billingId}")
    @Operation(summary = "Get payments by billing", description = "Get all payments for a specific billing record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Billing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<PaymentResponse>> getByBilling(
            @Parameter(description = "Billing ID") @PathVariable UUID billingId) {
        List<PaymentResponse> response = paymentService.findByBillingId(billingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a specific payment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentResponse> getById(
            @Parameter(description = "Payment ID") @PathVariable UUID id) {
        PaymentResponse response = paymentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Record payment", description = "Record a new payment against a billing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment recorded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentResponse> recordPayment(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Valid @RequestBody PaymentRecordRequest request) {
        PaymentResponse response = paymentService.recordPayment(request, seller.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}