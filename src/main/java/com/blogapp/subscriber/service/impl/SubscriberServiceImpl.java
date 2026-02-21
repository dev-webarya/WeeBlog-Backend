package com.blogapp.subscriber.service.impl;

import com.blogapp.common.dto.PageResponse;
import com.blogapp.common.exception.BadRequestException;
import com.blogapp.common.exception.ResourceNotFoundException;
import com.blogapp.otp.enums.OtpPurpose;
import com.blogapp.otp.service.OtpService;
import com.blogapp.subscriber.dto.request.SubscribeRequest;
import com.blogapp.subscriber.dto.response.SubscriberResponse;
import com.blogapp.subscriber.entity.Subscriber;
import com.blogapp.subscriber.enums.SubscriberStatus;
import com.blogapp.subscriber.mapper.SubscriberMapper;
import com.blogapp.subscriber.repository.SubscriberRepository;
import com.blogapp.subscriber.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final OtpService otpService;

    @Override
    public SubscriberResponse startSubscription(SubscribeRequest request) {
        Optional<Subscriber> existing = subscriberRepository.findByEmail(request.getEmail());

        if (existing.isPresent()) {
            Subscriber sub = existing.get();
            if (sub.getStatus() == SubscriberStatus.ACTIVE && sub.isVerified()) {
                throw new BadRequestException("This email is already subscribed.");
            }
            // Re-subscribe (was unsubscribed) — resend OTP
            sub.setStatus(SubscriberStatus.ACTIVE);
            sub.setName(request.getName());
            sub.setUnsubscribedAt(null);
            subscriberRepository.save(sub);
        } else {
            Subscriber subscriber = Subscriber.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .status(SubscriberStatus.ACTIVE)
                    .verified(false)
                    .build();
            subscriberRepository.save(subscriber);
        }

        // Send OTP
        otpService.sendOtp(request.getEmail(), OtpPurpose.SUBSCRIBE);
        log.info("Subscription OTP sent to: {}", request.getEmail());

        Subscriber saved = subscriberRepository.findByEmail(request.getEmail()).orElseThrow();
        return subscriberMapper.toResponse(saved);
    }

    @Override
    public SubscriberResponse verifySubscription(String email, String otp) {
        otpService.verifyOtp(email, otp, OtpPurpose.SUBSCRIBE);

        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "email", email));

        subscriber.setVerified(true);
        subscriber = subscriberRepository.save(subscriber);

        log.info("Subscriber verified: {}", email);
        return subscriberMapper.toResponse(subscriber);
    }

    @Override
    public void unsubscribe(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber", "email", email));

        subscriber.setStatus(SubscriberStatus.UNSUBSCRIBED);
        subscriber.setUnsubscribedAt(LocalDateTime.now());
        subscriberRepository.save(subscriber);

        log.info("Subscriber unsubscribed: {}", email);
    }

    @Override
    public PageResponse<SubscriberResponse> getAllSubscribers(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Subscriber> subPage;

        if (status != null && !status.isBlank()) {
            try {
                SubscriberStatus subStatus = SubscriberStatus.valueOf(status.toUpperCase());
                subPage = subscriberRepository.findByStatus(subStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        } else {
            subPage = subscriberRepository.findAll(pageable);
        }

        List<SubscriberResponse> content = subPage.getContent().stream()
                .map(subscriberMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<SubscriberResponse>builder()
                .content(content)
                .page(subPage.getNumber())
                .size(subPage.getSize())
                .totalElements(subPage.getTotalElements())
                .totalPages(subPage.getTotalPages())
                .first(subPage.isFirst())
                .last(subPage.isLast())
                .build();
    }
}
