package com.blogapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoAuditConfig {
    // Enables @CreatedDate and @LastModifiedDate annotations on Mongo documents
}
