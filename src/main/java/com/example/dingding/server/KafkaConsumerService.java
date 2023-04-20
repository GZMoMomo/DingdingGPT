package com.example.dingding.server;


import com.example.dingding.pojo.openai_embeddings_product_data;
import com.example.dingding.pojo.user_send;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.example.dingding.utils.parseJson;
import com.example.dingding.utils.milvus;
import com.example.dingding.mapper.openai_embeddings_product_dataMapper;
import com.example.dingding.server.serverImpl.gptApiKnowledgeEmbedding;

@Service
public class KafkaConsumerService {
    @Autowired
    sendMsg sendMsg;
    @Autowired
    getMsg getmsg;
    @Autowired
    parseJson parseJson;
    @Autowired
    AdminClient adminClient;
    @Autowired
    KafkaConsumer kafkaConsumer;
    @Autowired
    AdminClient adminClientKnowledge;
    @Autowired
    KafkaConsumer kafkaConsumerKnowledge;
    @Autowired
    milvus milvus;
    @Autowired
    openai_embeddings_product_dataMapper openaiEmbeddingsProductDataMapper;



    /**
     * 监听生产者发送的信息，消费信息
     * @param record 回调函数
     * @param ack 手动确认消费信息
     * @throws IOException
     */
   @KafkaListener(topics = "user_send2",groupId = "consumer_user",containerFactory = "kafkaListenerContainerFactory")
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

    /**
     * 接入向量数据库milvus
     * 监听生产者发送的信息，消费信息
     * @param record 回调函数
     * @param ack 手动确认消费信息
     * @throws IOException
     */
    @KafkaListener(topics = "knowledge",groupId = "knowledge",containerFactory = "kafkaListenerContainerFactoryKnowledge")
    public void consumeMessageKnowledge(ConsumerRecord<String,String> record, Acknowledgment ack) throws IOException {
        try {
            String message=record.value();
            //将钉钉@的信息保存至user_send对象中
            user_send user=parseJson.parseJson(message,user_send.class);
            //将用户问题向量化
            user=getmsg.getMsgKnowledgeEmbedding(user);
            //使用向量化后的数据与milvus做相似度匹配，返回最接近的两条数据。
            List<Integer> ids=milvus.returnResultId(user);
            List<openai_embeddings_product_data> openaiEmbeddingsProductDatas=new ArrayList<>();
            for(Integer id:ids){
                openaiEmbeddingsProductDatas.add(openaiEmbeddingsProductDataMapper.selectById(id));
            }
            //将用户的问题和从milvus得到的文本结合，发送请求到gpt
            user_send userFinally=getmsg.getMsgKnowledge(user,openaiEmbeddingsProductDatas);
            //最终答案
            sendMsg.sendMsgKnowledge(userFinally);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 最新消息的偏移量减去已消费的偏移量，并将所有分区的消费延迟求和,返回所有分区的平均消费延迟。
     * @return lag / count
     * @throws TimeoutException
     */
    public  long lagOf_usersend() throws TimeoutException {
        try {
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets("consumer_user");
            try {
                //Kafka服务器获取指定消费者组中所有分区的当前消费位移信息,超过10秒抛异常
                Map<TopicPartition, OffsetAndMetadata> consumedOffsets = result.partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);
                try {
                    Map<TopicPartition, Long> endOffsets;
                    synchronized (kafkaConsumer) {
                        //获取所有 TopicPartition 最新的 offset
                        endOffsets = kafkaConsumer.endOffsets(consumedOffsets.keySet());
                    }
                    /*long lag = endOffsets.entrySet().stream()
                            .filter(entry -> entry.getKey().topic().equals("user_send"))
                            .mapToLong(entry -> entry.getValue() - consumedOffsets.get(entry.getKey()).offset())
                            .sum();*/
                    long lag = 0;
                    long count = 0;
                    //遍历所有分区
                    for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
                        //若分区主题等于 user_send2
                        if (entry.getKey().topic().equals("user_send2")) {
                            //最新的offset - 当前的offset = 前方还有几条消息要消费
                            long partitionLag = entry.getValue() - consumedOffsets.get(entry.getKey()).offset();
                            lag += partitionLag;
                            count++;
                        }
                    }
                    //平均所有分区算的前方还有几条消息要消费
                    return  lag / count;
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断异常
                // ...
                return -1;
            } catch (ExecutionException e) {
                // 处理 ExecutionException
                // ...
                return -1;
            } catch (TimeoutException e) {
                throw new TimeoutException("Timed out when getting lag for consumer group " + "consumer_user");
            } catch (java.util.concurrent.TimeoutException e) {
                e.printStackTrace();
                return -1;
            }
        } finally {

        }
    }



    /**
     * 接入知识库milvus
     * 最新消息的偏移量减去已消费的偏移量，并将所有分区的消费延迟求和,返回所有分区的平均消费延迟。
     * @return lag / count
     * @throws TimeoutException
     */
    public  long lagOf_knowledge() throws TimeoutException {
        try {
            ListConsumerGroupOffsetsResult result = adminClientKnowledge.listConsumerGroupOffsets("knowledge");
            try {
                //Kafka服务器获取指定消费者组中所有分区的当前消费位移信息,超过10秒抛异常
                Map<TopicPartition, OffsetAndMetadata> consumedOffsets = result.partitionsToOffsetAndMetadata().get(10, TimeUnit.SECONDS);
                try {
                    Map<TopicPartition, Long> endOffsets;
                    synchronized (kafkaConsumerKnowledge) {
                        //获取所有 TopicPartition 最新的 offset
                        endOffsets = kafkaConsumerKnowledge.endOffsets(consumedOffsets.keySet());
                    }
                    /*long lag = endOffsets.entrySet().stream()
                            .filter(entry -> entry.getKey().topic().equals("user_send"))
                            .mapToLong(entry -> entry.getValue() - consumedOffsets.get(entry.getKey()).offset())
                            .sum();*/
                    long lag = 0;
                    long count = 0;
                    //遍历所有分区
                    for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
                        //若分区主题等于 user_send2
                        if (entry.getKey().topic().equals("knowledge")) {
                            //最新的offset - 当前的offset = 前方还有几条消息要消费
                            long partitionLag = entry.getValue() - consumedOffsets.get(entry.getKey()).offset();
                            lag += partitionLag;
                            count++;
                        }
                    }
                    //平均所有分区算的前方还有几条消息要消费
                    return  lag / count;
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断异常
                // ...
                return -1;
            } catch (ExecutionException e) {
                // 处理 ExecutionException
                // ...
                return -1;
            } catch (TimeoutException e) {
                throw new TimeoutException("Timed out when getting lag for consumer group " + "consumer_user");
            } catch (java.util.concurrent.TimeoutException e) {
                e.printStackTrace();
                return -1;
            }
        } finally {

        }
    }
}
