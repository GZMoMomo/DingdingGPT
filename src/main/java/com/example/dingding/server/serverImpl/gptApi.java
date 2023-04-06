package com.example.dingding.server.serverImpl;


import com.example.dingding.pojo.user_send;
import org.json.JSONArray;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class gptApi {


    @Autowired
    HttpUtils http;


    /**
     * 将钉钉@收到的信息和用户历史聊天记录发送至GPT API接口
     * @param user 用户对象，存储了用户的信息
     * @param userSendList 用户对象集合，存储了用户历史信息
     * @return  API返回的json字符串
     * @throws IOException
     */
    public String gptApi(user_send user, List<user_send> userSendList) throws IOException {
        String content = cleanContent(user.getContent());

        //添加JSON
        JSONObject requestBody = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        //message 添加用户问题
            //循环添加历史聊天记录
        for(user_send user_send:userSendList){
            JSONObject usermessageList = new JSONObject();
            usermessageList.put("role","user");
            usermessageList.put("content",user_send.getContent());
            JSONObject answermessageList = new JSONObject();
            answermessageList.put("role","assistant");
            answermessageList.put("content",user_send.getAnswer());
            messagesArray.put(usermessageList);
            messagesArray.put(answermessageList);
        }
        message.put("role","user");
        message.put("content",content);
        //messageArray
        messagesArray.put(message);
        //JsonObject 设置GPT参数
        requestBody.put("messages", messagesArray);
        requestBody.put("temperature", 1);
        requestBody.put("model", "gpt-3.5-turbo");
        //执行
        return http.post(http.url,requestBody,http.token);
    }

    /**
     * 将钉钉@收到的信息发送至GPT API图像接口
     * @param user 用户对象，存储了用户的信息
     * @return  API返回的json字符串
     * @throws IOException
     */
    public String gptApiImage(user_send user) throws IOException {

        String content = cleanContent(user.getContent());

        //添加JSON
        JSONObject requestBody = new JSONObject();

        //JsonObject 设置GPT参数
        requestBody.put("prompt", content);
        requestBody.put("n", 2);
        requestBody.put("size", "1024x1024");
        //执行
        return http.post(http.urlImage,requestBody,http.token);
    }


    public String cleanContent(String content){
        //去除文本前后空格
        StringBuilder stringBuilder=new StringBuilder(content.trim());
        for(int i=0;i<stringBuilder.length();i++){
            //去除回车换行符
            if(stringBuilder.charAt(i)=='\n' || stringBuilder.charAt(i)=='\r'){
                stringBuilder.replace(i,i+1," ");
            }
        }

        //截取文本末尾问号
        if(stringBuilder.charAt(stringBuilder.length()-1)=='?'){
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return content;
    }
}
