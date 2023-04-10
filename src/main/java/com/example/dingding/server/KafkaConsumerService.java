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

@Service
public class KafkaConsumerService {
    @Autowired
    sendMsg sendMsg;
    @Autowired
    getMsg getmsg;

   @KafkaListener(topics = "user_send")
    public void consumeMessage(ConsumerRecord<String,String> record, Acknowledgment ack) throws IOException {
        try {
            String message=record.value();
            System.out.println(message);
            JSONObject json= JSON.parseObject(message);
            user_send user=new user_send();
            user=getmsg.getMsg(json,user);
            sendMsg.sendMsg(user);
        }catch (Exception e){
            e.printStackTrace();
            String message=record.value();
            JSONObject json= JSON.parseObject(message);
            user_send user=new user_send();
            user=getmsg.getMsg(json,user);
            sendMsg.freeText(user,"Ops!!系统出现严重错误！请管理员尽快维护!");
        }finally {
            ack.acknowledge();
        }
    }

}
