package com.example.dingding.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dingding.mapper.user_sendMapper;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.getMsg;


import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.utils.AliGetImage;
import com.example.dingding.utils.RedisConfig;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.example.dingding.server.sendMsg;
import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.mapper.user_sendMapper;
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

    ExecutorService executorService= Executors.newFixedThreadPool(10);

    /**
     * 接受钉钉@发送的json对象，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/getMsg",method= RequestMethod.POST)
    public void getMsgAndSend(@RequestBody(required = false) JSONObject json) throws IOException {
        executorService.submit(()->{
            try {
                user_send user=getmsg.getMsg(json);
                sendMsg.sendMsg(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 接受钉钉@发送的json对象，将json对象交由server层处理
     * @param json 钉钉@发送的json对象
     * @throws IOException
     */
    @RequestMapping(value="/getImage",method= RequestMethod.POST)
    public void getImageAndSend(@RequestBody(required = false) JSONObject json) throws IOException {
        executorService.submit(()->{
            try {
                user_send user=getmsg.getImage(json);
                sendMsg.sendImageUrl(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
        user_send user_send=new user_send();
        user_send.setContent("宇航员猫咪");
        System.out.println(gptApi.gptApiImage(user_send));
    }
}
