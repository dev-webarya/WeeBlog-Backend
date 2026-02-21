package com.blogapp.subscriber.service;

import com.blogapp.subscriber.dto.request.SubscribeRequest;
import com.blogapp.subscriber.dto.response.SubscriberResponse;
import com.blogapp.common.dto.PageResponse;

public interface SubscriberService {

    SubscriberResponse startSubscription(SubscribeRequest request);

    SubscriberResponse verifySubscription(String email, String otp);

    void unsubscribe(String email);

    PageResponse<SubscriberResponse> getAllSubscribers(String status, int page, int size);
}
