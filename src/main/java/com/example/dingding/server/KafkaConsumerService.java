package com.example.dingding.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dingding.pojo.user_send;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import java.io.IOException;
import com.example.dingding.utils.parseJson;

@Service
public class KafkaConsumerService {
    @Autowired
    sendMsg sendMsg;
    @Autowired
    getMsg getmsg;
    @Autowired
    parseJson parseJson;

   @KafkaListener(topics = "user_send")
    public void consumeMessage(ConsumerRecord<String,String> record, Acknowledgment ack) throws IOException {
        try {
            String message=record.value();
            //将钉钉@的信息保存至user_send对象中
            user_send user=parseJson.parseJson(message,user_send.class);
            user=getmsg.getMsg(user);
            sendMsg.sendMsg(user);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            ack.acknowledge();
        }
    }

}
