package com.example.dingding.server;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.dingding.mapper.user_sendMapper;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.serverImpl.gptApi_specialSet;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.mapper.user_sendMapper;

@Service
public class getMsg {
    @Autowired
    user_send user;
    @Autowired
    gptApi gptApi;
    @Autowired
    gptApi_specialSet gptApi_specialSet;
    @Autowired
    user_sendMapper user_sendMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     *  获取钉钉@的JSON信息，然后发送至GPT API 获取答案，最终输出答案到钉钉
     * @param json  获取钉钉@后JSON信息
     * @throws IOException
     */
    public user_send getMsg(JSONObject json) throws IOException {
        //将钉钉@的信息保存至user_send对象中
        user.setuser(json);
        //获取历史聊天记录
        List<user_send> userSendList=userSendListRedis(user);
        //执行GPT API
        String answer=gptApi.gptApi(user,userSendList);
        //将GPT API响应的回答保存至user_send对象中
        user.setans(answer);
        return user;
    }

    /**
     *  获取钉钉@的JSON信息，然后发送至GPT API Image 获取图片，最终输出图片到钉钉
     * @param json  获取钉钉@后JSON信息
     * @throws IOException
     */
    public user_send getImage(JSONObject json) throws IOException {
        //将钉钉@的信息保存至user_send对象中
        user.setuser(json);
        //发送给chatgpt优化描述词
        String new_content=gptApi_specialSet.gptApiImageSet(user);
        //保存优化后的描述词
        user.setans(new_content);
        //插入记录到数据库
        user.setGptApiType("chatUpImage");
        user_sendMapper.insert(user);
        //设置描述词为优化后的描述词
        user_send new_user=user;
        new_user.setContent(user.getAnswer());
        System.out.println("新描述："+user.getAnswer());
        //执行GPTIimage API
        String answer=gptApi.gptApiImage(new_user);
        //将GPT API响应的回答保存至user_send对象中
        new_user.setansImage(answer);
        return new_user;
    }

    /**
     * 获取user历史聊天记录 mysql版本
     * @param user
     * @return
     */
    public List<user_send> userSendList(user_send user){
        //只获取10分钟前的记录
        QueryWrapper<user_send> userSendQueryWrapper=new QueryWrapper<user_send>();
        List<user_send> userSendList=user_sendMapper.selectList(userSendQueryWrapper.eq("senderStaffId",user.getSenderStaffId()).ge("createAt",Long.valueOf(user.getCreateAt())-600000).orderBy(true,true,"id"));
        return userSendList;
    }

    /**
     * 获取user历史聊天记录 redis版本
     * @param user
     * @return
     */
    public List<user_send> userSendListRedis(user_send user){
        List<user_send> userSendList=new ArrayList<user_send>();
        //模糊匹配用户ID
        Set<String> keys=redisTemplate.keys(user.getSenderStaffId()+"_"+"*");
        //遍历聊天记录保存在List中
        for (String key:keys) {
            user_send userSend=(user_send) redisTemplate.opsForValue().get(key);
            userSendList.add(userSend);
        }
        return userSendList;
    }



}