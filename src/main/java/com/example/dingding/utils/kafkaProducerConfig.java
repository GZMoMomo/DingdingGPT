package com.example.dingding.utils;

import com.example.dingding.pojo.user_send;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(kafkaProperties.class)
public class kafkaProducerConfig {
    private final kafkaProperties kafkaProperties;

    public kafkaProducerConfig(kafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Producer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers",kafkaProperties.getBootstrapServers());
        props.put("retries",kafkaProperties.getRetries());
        props.put("acks",kafkaProperties.getAcks());
        props.put("key.serializer",kafkaProperties.getKeySerializer());
        props.put("value.serializer",kafkaProperties.getValueSerializer());
        return new KafkaProducer<>(props);
    }
}
