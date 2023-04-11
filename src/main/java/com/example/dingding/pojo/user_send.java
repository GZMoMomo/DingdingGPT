package com.example.dingding.pojo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class user_send  implements Serializable {
    //会话id
    String conversationId;

    //群聊会话标题
    String conversationTitle;

    //企业内部群中@该机器人的成员userid
    String senderStaffId;

    //发送者昵称
    String senderNick;

    //当前会话的webhook地址
    String sessionWebhook;

    //消息时间戳
    String createAt;

    //消息类型
    String msgtype;

    //消息文本
    String content;

    //回复
    String answer;

    //回复时间戳
    String ansCreated;

    //prompt令牌数
    Integer prompt_tokens;

    //完成令牌数
    Integer completion_tokens;

    //总令牌数
    Integer total_tokens;

    //API类型
    String gptApiType;

    /**
     * 根据钉钉@接受信息，将收到的信息存储在user_send中
     * @param json
     */
    public void setuser(JSONObject json){
        content=(json.getJSONObject("text").get("content").toString().replaceAll(" ",""));
        senderStaffId=(json.getString("senderStaffId"));
        sessionWebhook=(json.getString("sessionWebhook"));
        msgtype=(json.getString("msgtype"));
        createAt=(json.getString("createAt"));
        conversationId=(json.getString("conversationId"));
        senderNick=(json.getString("senderNick"));
        conversationTitle=(json.getString("conversationTitle"));
    }

    /**
     * 根据GPT API返回的信息，将收到的信息存储在user_send中
     * @param json
     */
    public void setans(String json){
        org.json.JSONObject jsonObject1=new org.json.JSONObject(json);
        answer=jsonObject1.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        ansCreated=String.valueOf(jsonObject1.getInt("created"));
        prompt_tokens=jsonObject1.getJSONObject("usage").getInt("prompt_tokens");
        completion_tokens=jsonObject1.getJSONObject("usage").getInt("completion_tokens");
        total_tokens=jsonObject1.getJSONObject("usage").getInt("total_tokens");
    }

    /**
     * 根据GPT API Image返回的信息，将收到的信息存储在user_send中
     * @param json
     */
    public void setansImage(String json){
        org.json.JSONObject jsonObject1=new org.json.JSONObject(json);
        answer=jsonObject1.getJSONArray("data").toString();
        ansCreated=String.valueOf(jsonObject1.getInt("created"));
    }


}
