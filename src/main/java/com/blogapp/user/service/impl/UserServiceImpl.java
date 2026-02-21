package com.blogapp.user.service.impl;

import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;
import com.blogapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findOrCreateByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email.toLowerCase().trim())
                                .createdAt(LocalDateTime.now())
                                .build()));
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    @Override
    public User markEmailVerified(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setEmailVerifiedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User updateProfile(String userId, String name, String mobile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (name != null)
            user.setName(name);
        if (mobile != null)
            user.setMobile(mobile);
        return userRepository.save(user);
    }
}
