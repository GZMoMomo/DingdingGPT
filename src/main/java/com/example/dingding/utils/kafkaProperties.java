package com.example.dingding.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
@ConfigurationProperties(prefix = "kafka")
@Data
public class kafkaProperties {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.batch-size}")
    private int producerBatchSize;
    @Value("${kafka.producer.buffer-memory}")
    private int bufferMemory;
    @Value("${kafka.producer.acks}")
    private String acks;
    @Value("${kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${kafka.producer.value-serializer}")
    private String valueSerializer;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
}
