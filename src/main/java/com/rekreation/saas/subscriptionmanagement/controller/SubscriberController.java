package com.rekreation.saas.subscriptionmanagement.controller;

import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriberCreateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriberUpdateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.SubscriberResponse;
import com.rekreation.saas.subscriptionmanagement.security.CurrentSeller;
import com.rekreation.saas.subscriptionmanagement.security.SellerPrincipal;
import com.rekreation.saas.subscriptionmanagement.service.SubscriberService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscribers")
@RequiredArgsConstructor
@Tag(name = "Subscribers", description = "Subscriber management endpoints")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @GetMapping
    @Operation(summary = "List all subscribers", description = "Get paginated list of subscribers with optional search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PageResponse<SubscriberResponse>> getAll(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Search term for name, email, or phone")
            @RequestParam(required = false) String search,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<SubscriberResponse> response;
        if (search != null && !search.isBlank()) {
            response = subscriberService.searchBySeller(seller.getId(), search, pageable);
        } else {
            response = subscriberService.findAllBySeller(seller.getId(), pageable);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscriber by ID", description = "Retrieve a specific subscriber by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriber found",
                    content = @Content(schema = @Schema(implementation = SubscriberResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriberResponse> getById(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscriber ID") @PathVariable UUID id) {
        SubscriberResponse response = subscriberService.findById(id, seller.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create subscriber", description = "Create a new subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscriber created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriberResponse> create(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Valid @RequestBody SubscriberCreateRequest request) {
        SubscriberResponse response = subscriberService.create(request, seller.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscriber", description = "Update an existing subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriber updated successfully",
                    content = @Content(schema = @Schema(implementation = SubscriberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SubscriberResponse> update(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscriber ID") @PathVariable UUID id,
            @Valid @RequestBody SubscriberUpdateRequest request) {
        SubscriberResponse response = subscriberService.update(id, request, seller.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscriber", description = "Delete a subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscriber deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @CurrentSeller SellerPrincipal seller,
            @Parameter(description = "Subscriber ID") @PathVariable UUID id) {
        subscriberService.delete(id, seller.getId());
        return ResponseEntity.noContent().build();
    }
}