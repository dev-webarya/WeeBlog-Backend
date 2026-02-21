package com.blogapp.subscriber.entity;

import com.blogapp.subscriber.enums.SubscriberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscribers")
public class Subscriber {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String name;

    @Builder.Default
    private SubscriberStatus status = SubscriberStatus.ACTIVE;

    @Builder.Default
    private boolean verified = false;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime unsubscribedAt;
}
