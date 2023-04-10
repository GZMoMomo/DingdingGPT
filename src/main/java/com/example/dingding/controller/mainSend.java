package com.example.dingding.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.KafkaConsumerService;
import com.example.dingding.server.KafkaProducerSerivce;
import com.example.dingding.server.getMsg;


import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.utils.AliGetImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import com.example.dingding.server.sendMsg;


@RestController
public class mainSend {
    @Autowired
    getMsg getmsg;
    @Autowired
    sendMsg sendMsg;
    @Autowired
    gptApi gptApi;
    @Autowired
    AliGetImage aliGetImage;
    @Autowired
    KafkaProducerSerivce kafkaProducerSerivce;
    @Autowired
    KafkaConsumerService kafkaConsumerService;


    /**
     * 接受钉钉@发送的json对象，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/getMsg",method= RequestMethod.POST)
    public void getMsgAndSend(@RequestBody(required = false) JSONObject json) throws IOException {

                String message=json.toJSONString();
                kafkaProducerSerivce.sendMessage(message);
                /*user_send user=new user_send();
                user=getmsg.getMsg(json,user);
                sendMsg.sendMsg(user);*/

    }

    /**
     * 接受钉钉@发送的json对象，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/getImage",method= RequestMethod.POST)
    public void getImageAndSend(@RequestBody(required = false) JSONObject json) throws IOException {
            try {
                user_send user=new user_send();
                user=getmsg.getImage(json,user);
                sendMsg.sendImageUrl(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 接受钉钉@发送的图片和描述词，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/editImage",method= RequestMethod.POST)
    public void editImageAndSend(@RequestBody(required = false) JSONObject json) throws IOException {

        //aliGetImage.getAliImageUrl()
    }

    /**
     * 测试方式
     * @throws IOException
     */
    @RequestMapping(value="/test",method= RequestMethod.POST)
    public void test() throws IOException {

        kafkaProducerSerivce.sendMessage("{\"conversationId\":\"cidnx0wRB2/ApSkGhq1ewIygA==\",\"atUsers\":[{\"dingtalkId\":\"$:LWCP_v1:$uVgOt3kYDpPQyRGKDsZbCX+OQ8FGM/CT\"}],\"chatbotCorpId\":\"ding16559ddfdd3586b34ac5d6980864d335\",\"chatbotUserId\":\"$:LWCP_v1:$uVgOt3kYDpPQyRGKDsZbCX+OQ8FGM/CT\",\"msgId\":\"msg/YKrzd47wPi5xICTKO6+zg==\",\"senderNick\":\"莫铭浩\",\"isAdmin\":true,\"senderStaffId\":\"manager8022\",\"sessionWebhookExpiredTime\":1681117876415,\"createAt\":1681112474682,\"senderCorpId\":\"ding16559ddfdd3586b34ac5d6980864d335\",\"conversationType\":\"2\",\"senderId\":\"$:LWCP_v1:$Q/cQd9GZI7MN8N/DkFUJx/kF4Z0kI7ZQ\",\"conversationTitle\":\"儒韵GPT测试群\",\"isInAtList\":true,\"sessionWebhook\":\"https://oapi.dingtalk.com/robot/sendBySession?session=99807ae5868e89b27dd6994e702fde8a\",\"text\":{\"content\":\" 消费信息测试\"},\"robotCode\":\"dingeo8pq8qojlm6mkew\",\"msgtype\":\"text\"}\n");
        kafkaProducerSerivce.getQueueTimeAvg();
    }
}
