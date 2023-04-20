package com.example.dingding.server;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dingding.mapper.user_sendMapper;
import com.example.dingding.pojo.openai_embeddings_product_data;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.serverImpl.gptApi_specialSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.server.serverImpl.gptApiKnowledgeEmbedding;

@Service
public class getMsg {
    @Autowired
    sendMsg sendMsg;
    @Autowired
    gptApi gptApi;
    @Autowired
    gptApi_specialSet gptApi_specialSet;
    @Autowired
    gptApiKnowledgeEmbedding gptApiKnowledgeEmbedding;
    @Autowired
    user_sendMapper user_sendMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     *  获取钉钉@的JSON信息，然后发送至GPT API 获取答案，最终输出答案到钉钉
     * @throws IOException
     */
    public user_send getMsg(user_send user)  {
        sendMsg.freeText(user,"已经收到您的问题啦，请稍等片刻，正在努力思考中~");
        //获取历史聊天记录
        List<user_send> userSendList=userSendListRedis(user);
        //执行GPT API
        String answer=gptApi.gptApi(user,userSendList);
        //将GPT API响应的回答保存至user_send对象中
        user.setans(answer);
        return user;
    }

    /**
     *  获取钉钉@的JSON信息，然后发送至GPT API 获取答案，最终输出答案到钉钉
     * @throws IOException
     */
    public user_send getMsgKnowledge(user_send user,List<openai_embeddings_product_data> openaiEmbeddingsProductDatas)  {
        //获取历史聊天记录
        List<user_send> userSendList=userSendListRedis(user);
        //执行GPT API
        String answer=gptApiKnowledgeEmbedding.gptApiKnowledge(user,openaiEmbeddingsProductDatas,userSendList);
        //将GPT API响应的回答保存至user_send对象中
        user.setans(answer);
        return user;
    }


    /**
     * 接入向量数据库milvus,将用户的问题向量化
     *  获取钉钉@的JSON信息，然后发送至GPT API 获取答案，最终输出答案到钉钉
     * @throws IOException
     */
    public user_send getMsgKnowledgeEmbedding(user_send user)  {
        sendMsg.freeText(user,"已经收到您的问题啦，请稍等片刻，正在查询知识库中~");
        //执行GPT embedding api
        String answer=gptApiKnowledgeEmbedding.gptApiKnowledgeEmbedding(user);
        //将GPT embedding API响应的回答保存至user_send对象中
        user.setansKnowledgeEmbedding(answer);
        user.setGptApiType("embedding");
        user_sendMapper.insert(user);
        return user;
    }

    /**
     *  获取钉钉@的JSON信息，然后发送至GPT API Image 获取图片，最终输出图片到钉钉
     * @param json  获取钉钉@后JSON信息
     * @throws IOException
     */
    public user_send getImage(JSONObject json,user_send user) throws IOException {
        //将钉钉@的信息保存至user_send对象中
        user.setuser(json);
        sendMsg.freeText(user,"已经收到您的需求啦，请稍等片刻，正在帮您优化描述细节~");
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
        sendMsg.freeText(user,"已经帮您优化好描述词啦，请稍等片刻，正在帮您生成图像~\n"+user.getAnswer());
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
