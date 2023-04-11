package com.example.dingding.server;




import com.alibaba.fastjson.JSONObject;
import com.example.dingding.pojo.user_send;
import com.example.dingding.utils.kafkaProperties;
import com.example.dingding.utils.parseJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.util.Map;

@Service
public class KafkaProducerSerivce {
    @Autowired
    sendMsg sendMsg;

    private Producer<String, String> producer;
    public KafkaProducerSerivce(Producer<String, String> producer){
        this.producer= producer;
    }

    /**
     * kafka生产者发送信息
     * @param json 钉钉接受的jsonObject
     */
    public void sendMessage(JSONObject json){
        user_send user=new user_send();
        user.setuser(json);
        double queueTime=getQueueTimeAvg();
        if(!(queueTime<=1 || Double.isNaN(queueTime) || "0.00".equals(String.format("%.2f", queueTime))) ){
            sendMsg.freeText(user,"排队中~预计等待时间为："+queueTime+"秒");
        }
        try{
            String message= parseJson.toJson(user);
            ProducerRecord<String, String> record=new ProducerRecord<>("user_send",message);
            producer.send(record,(metadata,exception)->{
                if(exception!= null){
                    exception.printStackTrace();
                    sendMsg.freeText(user,"系统出现严重错误！请管理员尽快修复！用户请耐心等待~");
                }
            });
        }catch (Exception e){
            sendMsg.freeText(user,"系统出现严重错误！请管理员尽快修复！用户请耐心等待~");
        }
    }

    /**
     * 获取Kafka生产者中的消费消息的平均速度指标 /秒
     * @return
     */
    public double getQueueTimeAvg(){
        Map<MetricName, ? extends Metric> metrics = producer.metrics();
        double avgQueueTime = 0;
        int count = 0;
        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
            MetricName metricName = entry.getKey();
            Metric metric = entry.getValue();
            if (metricName.name().equals("record-queue-time-avg")) {
                avgQueueTime += (Double) metric.metricValue();
                count++;
            }
        }
        if (count > 0) {
            avgQueueTime /= count;
            return avgQueueTime;
        } else {
            return 0.00;
        }
    }

    /**
     * 发送消息后关闭生产者
     */
    @PreDestroy
    public void close(){
        producer.close();
    }


}
