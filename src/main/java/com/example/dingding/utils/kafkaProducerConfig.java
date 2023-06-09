package com.example.dingding.utils;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Properties;

@Configuration
@EnableKafka
@EnableConfigurationProperties(kafkaProperties.class)
public class kafkaProducerConfig {
    private final kafkaProperties kafkaProperties;

    public kafkaProducerConfig(kafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * //kafka注册配置信息实例化Producer
     * @return Producer
     */
    @Bean
    public Producer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers",kafkaProperties.getBootstrapServers());
        props.put("retries",kafkaProperties.getRetries());
        props.put("acks",kafkaProperties.getAcks());
        props.put("key.serializer",kafkaProperties.getKeySerializer());
        props.put("value.serializer",kafkaProperties.getValueSerializer());
        props.put("max.block.ms", kafkaProperties.getMaxBlockMs());
        props.put("session.timeout.ms",kafkaProperties.getSessionTimeoutMs());
        return new KafkaProducer<>(props);
    }
}
