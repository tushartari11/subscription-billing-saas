package com.rekreation.saas.subscriptionmanagement.controller;

import com.rekreation.saas.subscriptionmanagement.dto.response.BillingResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.security.CurrentSeller;
import com.rekreation.saas.subscriptionmanagement.security.SellerPrincipal;
import com.rekreation.saas.subscriptionmanagement.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billings")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Billing management endpoints")
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    @Operation(summary = "List all billings", description = "Get paginated list of billings for the current seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PageResponse<BillingResponse>> getAll(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BillingResponse> response = billingService.findBySellerId(seller.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriber/{subscriberId}")
    @Operation(summary = "Get billings by subscriber", description = "Get paginated billings for a specific subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PageResponse<BillingResponse>> getBySubscriber(
            @Parameter(description = "Subscriber ID") @PathVariable UUID subscriberId,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BillingResponse> response = billingService.findBySubscriberId(subscriberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get billing by ID", description = "Retrieve a specific billing record by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billing found",
                    content = @Content(schema = @Schema(implementation = BillingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Billing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BillingResponse> getById(
            @Parameter(description = "Billing ID") @PathVariable UUID id) {
        BillingResponse response = billingService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{billNumber}")
    @Operation(summary = "Get billing by bill number", description = "Retrieve a billing record by its bill number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billing found",
                    content = @Content(schema = @Schema(implementation = BillingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Billing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BillingResponse> getByBillNumber(
            @Parameter(description = "Bill number") @PathVariable String billNumber) {
        BillingResponse response = billingService.findByBillNumber(billNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue billings", description = "Get all overdue billings for the current seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue billings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BillingResponse>> getOverdue(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller) {
        List<BillingResponse> response = billingService.findOverdueBillings(seller.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate monthly bills", description = "Generate bills for all active subscriptions for a specific month")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bills generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid billing month"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BillingResponse>> generateMonthlyBills(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Billing month in yyyy-MM format", example = "2026-02")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth billingMonth) {
        List<BillingResponse> response = billingService.generateMonthlyBills(seller.getId(), billingMonth);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}