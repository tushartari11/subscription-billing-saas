package com.rekreation.saas.subscriptionmanagement.service;

import com.rekreation.saas.subscriptionmanagement.domain.Seller;
import com.rekreation.saas.subscriptionmanagement.dto.request.LoginRequest;
import com.rekreation.saas.subscriptionmanagement.dto.request.SellerRegisterRequest;
import com.rekreation.saas.subscriptionmanagement.dto.response.AuthResponse;
import com.rekreation.saas.subscriptionmanagement.dto.response.SellerResponse;
import com.rekreation.saas.subscriptionmanagement.exception.DuplicateResourceException;
import com.rekreation.saas.subscriptionmanagement.exception.ResourceNotFoundException;
import com.rekreation.saas.subscriptionmanagement.repository.SellerRepository;
import com.rekreation.saas.subscriptionmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(SellerRegisterRequest request) {
        if (sellerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Seller", "email", request.getEmail());
        }

        Seller seller = Seller.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .businessName(request.getBusinessName())
                .build();

        seller = sellerRepository.save(seller);

        String accessToken = jwtTokenProvider.generateAccessToken(seller);
        String refreshToken = jwtTokenProvider.generateRefreshToken(seller);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                SellerResponse.fromEntity(seller)
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Seller seller = findByEmail(request.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(seller);
        String refreshToken = jwtTokenProvider.generateRefreshToken(seller);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                SellerResponse.fromEntity(seller)
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Seller seller = findByEmail(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(seller);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(seller);

        return AuthResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                SellerResponse.fromEntity(seller)
        );
    }

    public Seller findByEmail(String email) {
        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "email", email));
    }

    public Seller findEntityById(UUID id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", id));
    }

    public SellerResponse findById(UUID id) {
        return SellerResponse.fromEntity(findEntityById(id));
    }
}