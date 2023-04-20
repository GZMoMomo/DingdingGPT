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
    //kafka集群地址
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    //生产者发送信息重试次数
    @Value("${kafka.producer.retries}")
    private int retries;
    //生产者发送信息时批量大小
    @Value("${kafka.producer.batch-size}")
    private int producerBatchSize;
    //生产者缓存可用于缓冲等待发送到服务器的消息的总字节数
    @Value("${kafka.producer.buffer-memory}")
    private int bufferMemory;
    //生产者期望从服务器接收的确认数。all表示需要所有副本都确认，0表示不需要确认。
    @Value("${kafka.producer.acks}")
    private String acks;
    //生产者使用的键序列化器
    @Value("${kafka.producer.key-serializer}")
    private String keySerializer;
    //生产者使用的值序列化器
    @Value("${kafka.producer.value-serializer}")
    private String valueSerializer;
    //消费者所属的消费组ID
    @Value("${kafka.consumer.group-id.user}")
    private String groupIdUser;
    @Value("${kafka.consumer.group-id.knowledge}")
    private String groupIdKnowledge;
    //消费者是否启用自动提交
    @Value("${kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    //当消费者找不到先前偏移时，它将从哪个位置开始读取
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    //一次调用poll()方法返回的最大记录数
    @Value("${kafka.consumer.max-poll-records}")
    private int maxPollRecords;
    //当生产者或消费者无法发送或接收消息时，它们将阻塞的最大时间
    @Value("${kafka.max.block.ms}")
    private long maxBlockMs;
    //两次消费信息的时间间隔，超时报错
    @Value("${kafka.consumer.max.poll.interval.ms}")
    private int maxPollIntervalMs;
    //心跳间隔时间
    @Value("${kafka.consumer.heartbeat.interval}")
    private int heartbeatInterval;
    //会话超时时间
    @Value("${kafka.session.timeout.ms}")
    private int sessionTimeoutMs;
}
