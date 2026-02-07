package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Subscriber;
import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriberCreateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.request.SubscriberUpdateRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.PageResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.SubscriberResponse;
import com.rekreation.saas.subscriptionmanagement.exception.ResourceNotFoundException;
import com.rekreation.saas.subscriptionmanagement.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SellerService sellerService;

    public PageResponse<SubscriberResponse> findAllBySeller(UUID sellerId, Pageable pageable) {
        Page<Subscriber> subscribers = subscriberRepository.findBySellerId(sellerId, pageable);
        return PageResponse.from(subscribers, SubscriberResponse::fromEntity);
    }

    public PageResponse<SubscriberResponse> searchBySeller(UUID sellerId, String search, Pageable pageable) {
        Page<Subscriber> subscribers = subscriberRepository.searchBySellerIdAndKeyword(sellerId, search, pageable);
        return PageResponse.from(subscribers, SubscriberResponse::fromEntity);
    }

    public SubscriberResponse findById(UUID id, UUID sellerId) {
        Subscriber subscriber = findByIdAndSellerId(id, sellerId);
        return SubscriberResponse.fromEntity(subscriber);
    }

    @Transactional
    public SubscriberResponse create(SubscriberCreateRequest request, UUID sellerId) {
        var seller = sellerService.findEntityById(sellerId);

        Subscriber subscriber = Subscriber.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .whatsappNumber(request.getWhatsappNumber() != null ?
                        request.getWhatsappNumber() : request.getPhoneNumber())
                .address(request.getAddress())
                .seller(seller)
                .build();

        subscriber = subscriberRepository.save(subscriber);
        return SubscriberResponse.fromEntity(subscriber);
    }

    @Transactional
    public SubscriberResponse update(UUID id, SubscriberUpdateRequest request, UUID sellerId) {
        Subscriber subscriber = findByIdAndSellerId(id, sellerId);

        if (request.getName() != null) {
            subscriber.setName(request.getName());
        }
        if (request.getEmail() != null) {
            subscriber.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            subscriber.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getWhatsappNumber() != null) {
            subscriber.setWhatsappNumber(request.getWhatsappNumber());
        }
        if (request.getAddress() != null) {
            subscriber.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            subscriber.setStatus(request.getStatus());
        }

        subscriber = subscriberRepository.save(subscriber);
        return SubscriberResponse.fromEntity(subscriber);
    }

    @Transactional
    public void delete(UUID id, UUID sellerId) {
        Subscriber subscriber = findByIdAndSellerId(id, sellerId);
        subscriberRepository.delete(subscriber);
    }

    public Subscriber findByIdAndSellerId(UUID id, UUID sellerId) {
        return subscriberRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));
    }

    public Subscriber findEntityById(UUID id) {
        return subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "id", id));
    }
}