package com.expense.splitter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB Configuration
 * Enables auditing for automatic timestamp management
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}