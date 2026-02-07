package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Subscriber;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription;
import com.rekreation.saas.subscriptionmanagement.domain.Subscription.SubscriptionStatus;
import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriptionCreateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.SubscriptionResponse;
import com.rekreation.saas.subscriptionmanagement.exception.BusinessValidationException;
import com.rekreation.saas.subscriptionmanagement.exception.ResourceNotFoundException;
import com.rekreation.saas.subscriptionmanagement.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriberService subscriberService;

    public List<SubscriptionResponse> findBySubscriberId(UUID subscriberId) {
        return subscriptionRepository.findBySubscriberId(subscriberId).stream()
                .map(SubscriptionResponse::fromEntity)
                .toList();
    }

    public List<SubscriptionResponse> findBySellerId(UUID sellerId) {
        return subscriptionRepository.findBySellerId(sellerId).stream()
                .map(SubscriptionResponse::fromEntity)
                .toList();
    }

    public SubscriptionResponse findById(UUID id) {
        Subscription subscription = findEntityById(id);
        return SubscriptionResponse.fromEntity(subscription);
    }

    @Transactional
    public SubscriptionResponse create(SubscriptionCreateRequest request, UUID sellerId) {
        Subscriber subscriber = subscriberService.findByIdAndSellerId(request.getSubscriberId(), sellerId);

        Subscription subscription = Subscription.builder()
                .name(request.getName())
                .description(request.getDescription())
                .amount(request.getAmount())
                .billingCycle(request.getBillingCycle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .subscriber(subscriber)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(subscription);
    }

    @Transactional
    public SubscriptionResponse cancel(UUID id, UUID sellerId) {
        Subscription subscription = findEntityById(id);

        if (!subscription.getSubscriber().getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("Subscription", "id", id);
        }

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new BusinessValidationException("Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(subscription);
    }

    @Transactional
    public SubscriptionResponse pause(UUID id, UUID sellerId) {
        Subscription subscription = findEntityById(id);

        if (!subscription.getSubscriber().getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("Subscription", "id", id);
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BusinessValidationException("Only active subscriptions can be paused");
        }

        subscription.setStatus(SubscriptionStatus.PAUSED);
        subscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(subscription);
    }

    @Transactional
    public SubscriptionResponse resume(UUID id, UUID sellerId) {
        Subscription subscription = findEntityById(id);

        if (!subscription.getSubscriber().getSeller().getId().equals(sellerId)) {
            throw new ResourceNotFoundException("Subscription", "id", id);
        }

        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new BusinessValidationException("Only paused subscriptions can be resumed");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(subscription);
    }

    public Subscription findEntityById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
    }
}