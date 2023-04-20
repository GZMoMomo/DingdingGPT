package com.example.dingding.server.serverImpl;

import com.example.dingding.pojo.openai_embeddings_product_data;
import com.example.dingding.pojo.user_send;
import com.example.dingding.server.sendMsg;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class gptApiKnowledgeEmbedding {
    @Autowired
    HttpUtils http;


    /**
     * 接入向量数据库，将用户问题和向量库的文本结合。
     * 将钉钉@收到的信息和用户历史聊天记录发送至GPT API接口
     * @param user 用户对象，存储了用户的信息
     * @return  API返回的json字符串
     * @throws IOException
     */
    public String gptApiKnowledge(user_send user, List<openai_embeddings_product_data> openai_embeddings_product_datas,List<user_send> userSendList) {
        //向量库返回的文本0
        openai_embeddings_product_data result0=openai_embeddings_product_datas.get(0);
        //向量库返回的文本1
        openai_embeddings_product_data result1=openai_embeddings_product_datas.get(1);
        String milvusResult0="\n产品名称："+result0.getProduct()+"\n问题关键字："+result0.getTitle()+"\n文本："+result0.getContent();
        String milvusResult1="\n产品名称："+result1.getProduct()+"\n问题关键字："+result1.getTitle()+"\n文本："+result1.getContent();
        //GPT预设
        String setApi="请结合下面的文本来回答问题，你明确下面产品名称是否与用户提问的产品相关，下面的文本内容是否与用户问题有关联，文本能否解决用户的问题。如果不能，你需要告知用户“当前知识库没有相关答案，但我可以给您提供一个相似例子”，然后结合文本进行回复。以下我提供的产品名称是空格分割字符串，分割的字符串都是同一款产品，你只需选用其中一个作为产品名称回复用户。请使用MarkDown格式返回信息。"
                +"知识库内容1："+milvusResult0+"知识库内容2："+milvusResult1;
        //返回知识库内容
        sendMsg sendMsg=new sendMsg();
        sendMsg.freeText(user,"知识库内容："+milvusResult0+milvusResult1);
        //清洗用户问题
        String content = cleanContent(user.getContent());
        //添加JSON
        JSONObject requestBody = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        JSONObject system = new JSONObject();
        //添加GPT system预设
        system.put("role","system");
        system.put("content",setApi);
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
        messagesArray.put(system);
        messagesArray.put(message);
        //JsonObject 设置GPT参数
        requestBody.put("messages", messagesArray);
        requestBody.put("temperature", 0);
        requestBody.put("model", "gpt-3.5-turbo");
        //流处理requestBody.put("stream", true);
        //执行
        return http.post(http.url,requestBody,http.token,user);
        //流处理httpSseUtils.postStream(http.url,requestBody,http.token,user);
    }



    /**将用户的问题通过embedding api转化为向量
     * 将钉钉@收到的信息和用户历史聊天记录发送至GPT API接口
     * @param user 用户对象，存储了用户的信息
     * @return  API返回的json字符串
     * @throws IOException
     */
    public String gptApiKnowledgeEmbedding(user_send user) {

        String content = cleanContent(user.getContent());
        //添加JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-embedding-ada-002");
        requestBody.put("input", content);
        //执行
        return http.post(http.urlEmbedding,requestBody,http.token,user);
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
