package com.example.dingding.server.serverImpl;



import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class HttpUtils {
    //HTTP客户端库，可用于向Web服务器发起HTTP请求并处理响应，单例复用实例。
    private static class HttpClientHolder{
        private static final OkHttpClient client=new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).protocols(Arrays.asList(Protocol.HTTP_1_1)).build();

    }

    //Jackson库中的一个核心类，它提供了序列化和反序列化JSON的功能,单例复用。
    private static class ObjectMapperHolder{
        private static final ObjectMapper objectMapper=new ObjectMapper();

    }

    //静态媒体类型，用来描述请求和响应消息的格式和字符集。
    private static final MediaType JSON = MediaType.get("application/json;chartset=utf-8");

    //GPT API地址，使用了代理服务器
    public static final String url = "https://mokjyz.xyz/v1/chat/completions";
    //GPTAPI Image
    public static final String urlImage = "https://mokjyz.xyz/v1/images/generations";
    // GPT API TOKEN
    public static final String token = "sk-S**" ;

    /**
     * 发送post请求给GPT API，接受返回的信息
     * @param url API地址
     * @param jsonObject 发送给GPT API的 json对象
     * @param apiKey GPT API TOKEN
     * @return  GPT API返回的json
     * @throws IOException
     */
    public static String post(String url, JSONObject jsonObject , String apiKey) throws IOException {
        System.out.println(jsonObject.toString());
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+apiKey)
                .post(body)
                .build();
        try (Response response = HttpClientHolder.client.newCall(request).execute()){
            if(!response.isSuccessful()) throw new IOException("Unexpected code "+response);
            ResponseBody responseBody=response.body();
            if(responseBody==null) throw new IOException("Empty response body");

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
        return ObjectMapperHolder.objectMapper.readValue(json, clazz);
    }
}
