package com.example.dingding.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

  /*  @KafkaListener(topics = "user_send")
    public void consumeMessage(ConsumerRecord<String,String> record, Acknowledgment ack){
        try {
            String message=record.value();
            JSONObject jsonObject= JSON.parseObject(message);
            System.out.println(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ack.acknowledge();
        }
    }*/
}
