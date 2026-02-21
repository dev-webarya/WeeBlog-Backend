package com.blogapp.subscriber.mapper;

import com.blogapp.subscriber.dto.response.SubscriberResponse;
import com.blogapp.subscriber.entity.Subscriber;
import org.springframework.stereotype.Component;

@Component
public class SubscriberMapper {

    public SubscriberResponse toResponse(Subscriber entity) {
        return SubscriberResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .status(entity.getStatus())
                .verified(entity.isVerified())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
