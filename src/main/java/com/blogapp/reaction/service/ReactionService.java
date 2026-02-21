package com.blogapp.reaction.service;

import com.blogapp.reaction.dto.request.ReactionRequest;
import com.blogapp.reaction.dto.response.ReactionResponse;

public interface ReactionService {

    ReactionResponse toggleReaction(String blogId, ReactionRequest request, String ipAddress);

    ReactionResponse getReactionStatus(String blogId, String visitorKey);
}
