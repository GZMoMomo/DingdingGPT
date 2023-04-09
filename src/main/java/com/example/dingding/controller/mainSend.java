package com.example.dingding.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.dingding.pojo.user_send;
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



    /**
     * 接受钉钉@发送的json对象，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/getMsg",method= RequestMethod.POST)
    public void getMsgAndSend(@RequestBody(required = false) JSONObject json) throws IOException {
            try {
               // String message=json.toJSONString();
               // kafkaProducerSerivce.sendMessage(message);

                user_send user=new user_send();
                user=getmsg.getMsg(json,user);
                sendMsg.sendMsg(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

       // kafkaProducerSerivce.sendMessage("123");

    }
}
