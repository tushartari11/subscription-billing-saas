package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Billing;
import com.rekreation.saas.subscriptionmanagement.domain.Payment;
import com.rekreation.saas.subscriptionmanagement.dto.request.PaymentRecordRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.PaymentResponse;
import com.rekreation.saas.subscriptionmanagement.exception.BusinessValidationException;
import com.rekreation.saas.subscriptionmanagement.exception.ResourceNotFoundException;
import com.rekreation.saas.subscriptionmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillingService billingService;

    public PageResponse<PaymentResponse> findBySellerId(UUID sellerId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findBySellerId(sellerId, pageable);
        return PageResponse.from(payments, PaymentResponse::fromEntity);
    }

    public PageResponse<PaymentResponse> findBySubscriberId(UUID subscriberId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findBySubscriberId(subscriberId, pageable);
        return PageResponse.from(payments, PaymentResponse::fromEntity);
    }

    public List<PaymentResponse> findByBillingId(UUID billingId) {
        return paymentRepository.findByBillingId(billingId).stream()
                .map(PaymentResponse::fromEntity)
                .toList();
    }

    public PaymentResponse findById(UUID id) {
        Payment payment = findEntityById(id);
        return PaymentResponse.fromEntity(payment);
    }

    @Transactional
    public PaymentResponse recordPayment(PaymentRecordRequest request, UUID sellerId) {
        Billing billing = billingService.findEntityById(request.getBillingId());

        if (!billing.getSubscriber().getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("Billing", "id", request.getBillingId());
        }

        BigDecimal outstanding = billing.getOutstandingAmount();
        if (request.getAmount().compareTo(outstanding) > 0) {
            throw new BusinessValidationException(
                    String.format("Payment amount (%s) exceeds outstanding amount (%s)",
                            request.getAmount(), outstanding));
        }

        Payment payment = Payment.builder()
                .paymentReference(generatePaymentReference())
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate() != null ?
                        request.getPaymentDate() : LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getTransactionId())
                .notes(request.getNotes())
                .billing(billing)
                .build();

        payment = paymentRepository.save(payment);

        billing.setPaidAmount(billing.getPaidAmount().add(request.getAmount()));
        billingService.updateBillingStatus(billing.getId());

        return PaymentResponse.fromEntity(payment);
    }

    public Payment findEntityById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}