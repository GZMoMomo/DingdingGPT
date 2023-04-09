package com.example.dingding.server;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "user_send")
    public void consumeMessage(ConsumerRecord<String,String> record, Acknowledgment ack){
        System.out.println("consumed:"+record.value());
        ack.acknowledge();
    }
}
