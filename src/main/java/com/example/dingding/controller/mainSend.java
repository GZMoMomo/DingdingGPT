package com.example.dingding.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.getMsg;


import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.utils.AliGetImage;
import com.example.dingding.utils.Log4j;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    Log4j log;

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
                Logger logger=log.log4j();
                logger.debug("对话接口用户传入信息JSON："+json.toString());
                user_send user=new user_send();
                user=getmsg.getMsg(json,user);
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
                Logger logger=log.log4j();
                logger.debug("生成图片接口用户传入信息JSON："+json.toString());
                user_send user=new user_send();
                user=getmsg.getImage(json,user);
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
        Logger logger=log.log4j();
        logger.debug("生成图片接口用户传入信息JSON：");
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
