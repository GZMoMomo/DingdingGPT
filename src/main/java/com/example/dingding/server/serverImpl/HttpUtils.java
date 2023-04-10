package com.example.dingding.server.serverImpl;

import com.example.dingding.pojo.user_send;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.dingding.server.sendMsg;
import okhttp3.*;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class HttpUtils {

    // 私有构造函数
    private HttpUtils() {}

    //HTTP客户端库，可用于向Web服务器发起HTTP请求并处理响应，单例复用实例。
    private static class SingletonHolder {
        private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3,TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).pingInterval(60,TimeUnit.MINUTES).build();
    }

    // 获取单例实例
    public static OkHttpClient getClient() {
        return SingletonHolder.client;
    }

    //Jackson库中的一个核心类，它提供了序列化和反序列化JSON的功能,单例复用。
    private static final ObjectMapper objectMapper=new ObjectMapper();

    //静态媒体类型，用来描述请求和响应消息的格式和字符集。
    private static final MediaType JSON = MediaType.get("application/json;chartset=utf-8");

    //GPT API地址，使用了代理服务器
    public static final String url = "https://mokjyz.xyz/v1/chat/completions";
    //GPTAPI Image
    public static final String urlImage = "https://mokjyz.xyz/v1/images/generations";
    // GPT API TOKEN
    public static final String token = "sk-**" ;

    /**
     * 发送post请求给GPT API，接受返回的信息
     * @param url API地址
     * @param jsonObject 发送给GPT API的 json对象
     * @param apiKey GPT API TOKEN
     * @return  GPT API返回的json
     * @throws IOException
     */
    public static String post(String url, JSONObject jsonObject , String apiKey, user_send user) throws IOException {
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+apiKey)
                .post(body)
                .build();
        try (Response response = getClient().newCall(request).execute()){
            if(!response.isSuccessful()){
                sendMsg sendMsg=new sendMsg();
                sendMsg.freeText(user,"ops!由于网络拥堵，来自大洋彼岸的回复丢失了！请稍后再试，若长时间失败，我的管理员正在奋力修复中，请耐心等待~");
                throw new IOException("Unexpected code: "+response);
            }
            ResponseBody responseBody=response.body();
            if(responseBody==null){
                sendMsg sendMsg=new sendMsg();
                sendMsg.freeText(user,"ops!由于未知原因，来自大洋彼岸的回复是空信息。请稍后再试，若长时间失败，我的管理员正在奋力修复中，请耐心等待~");
                throw new IOException("Empty response body");
            }
           //return jsonObject1.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            return responseBody.string();
        }

    }

    /**
     * 用于将JSON字符串解析成指定类型的Java对象。其中，参数json是待解析的JSON字符串，clazz是指定的目标Java类。
     * @param json
     * @param clazz
     * @param <T>
     * @return  指定类型的Java对象
     * @throws IOException
     */
    public static <T> T parseJson(String json,Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }
}
