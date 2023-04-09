package com.example.dingding.server;



import com.example.dingding.pojo.user_send;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import javax.annotation.PreDestroy;

@Service
public class KafkaProducerSerivce {


    private Producer<String, String> producer;
    public KafkaProducerSerivce(Producer<String, String> producer){
        this.producer= producer;
    }

    public void sendMessage(String message){
        ProducerRecord<String, String> record=new ProducerRecord<>("user_send",message);
        producer.send(record,(metadata,exception)->{
            if(exception!= null){
                exception.printStackTrace();
            }else {
                System.out.println("offset:"+metadata.offset());
            }
        });
    }

    @PreDestroy
    public void close(){
        producer.close();
    }
}
