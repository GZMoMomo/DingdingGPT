package com.example.dingding.server;




import com.example.dingding.utils.kafkaProperties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.util.Map;

@Service
public class KafkaProducerSerivce {


    private Producer<String, String> producer;
    private kafkaProperties kafkaproperties;
    public KafkaProducerSerivce(Producer<String, String> producer,kafkaProperties kafkaProperties){
        this.producer= producer;
        this.kafkaproperties=kafkaProperties;
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

    public String getQueueTimeAvg(){
        DecimalFormat df=new DecimalFormat("#.00");
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
            System.out.println("Average record queue time: " + avgQueueTime);
            return df.format(avgQueueTime);
        } else {
            System.out.println("No record queue time metric found");
            return "0.00";
        }
    }

    @PreDestroy
    public void close(){
        producer.close();
    }


}
