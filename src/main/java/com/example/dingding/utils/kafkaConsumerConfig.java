package com.example.dingding.utils;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(kafkaProperties.class)
@EnableKafka
public class kafkaConsumerConfig {

    private final kafkaProperties kafkaProperties;

    public kafkaConsumerConfig(com.example.dingding.utils.kafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }


    /**
     * 创建Kafka消费者实例，其中通过kafkaProperties中的配置获取所需的Kafka服务器地址，消费组ID，自动提交偏移量等参数
     *
     * @return ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdUser());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMaxPollIntervalMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaProperties.getHeartbeatInterval());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }


    /**
     * Kafka侦听器容器工厂，可以创建Kafka消息监听器容器实例，通过设置消费者工厂、手动确认模式等属性来自定义容器的行为
     *
     * @return ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    /**
     * 创建AdminClient，主要用于管理Kafka集群。
     * 创建和删除topic
     * 修改topic的配置
     * 获取Kafka集群的元数据信息
     * 增加或删除broker
     * 增加或删除分区
     * @return AdminClient
     */
    @Bean
    public AdminClient adminClient(){
        Properties props=new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit()); // 禁止自动提交位移
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdUser());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return AdminClient.create(props);
    }

    /**
     * 消费数据的客户端API
     * @return KafkaConsumer
     */
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer(){
        Properties props=new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit()); // 禁止自动提交位移
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdUser());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
       return new KafkaConsumer<String, String>(props);
    }



    //向量数据库

    /**
     * 创建Kafka消费者实例，其中通过kafkaProperties中的配置获取所需的Kafka服务器地址，消费组ID，自动提交偏移量等参数
     *
     * @return ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactoryKnowledge() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdKnowledge());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMaxPollIntervalMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaProperties.getHeartbeatInterval());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }


    /**
     * Kafka侦听器容器工厂，可以创建Kafka消息监听器容器实例，通过设置消费者工厂、手动确认模式等属性来自定义容器的行为
     *
     * @return ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryKnowledge() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryKnowledge());
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    /**
     * 创建AdminClient，主要用于管理Kafka集群。
     * 创建和删除topic
     * 修改topic的配置
     * 获取Kafka集群的元数据信息
     * 增加或删除broker
     * 增加或删除分区
     * @return AdminClient
     */
    @Bean
    public AdminClient adminClientKnowledge(){
        Properties props=new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit()); // 禁止自动提交位移
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdKnowledge());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return AdminClient.create(props);
    }

    /**
     * 消费数据的客户端API
     * @return KafkaConsumer
     */
    @Bean
    public KafkaConsumer<String, String> kafkaConsumerKnowledge(){
        Properties props=new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit()); // 禁止自动提交位移
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupIdKnowledge());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<String, String>(props);
    }


}
