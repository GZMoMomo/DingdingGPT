package com.example.dingding.server.serverImpl;

import com.example.dingding.pojo.user_send;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service    //SSE流式处理response
public class HttpSseUtils {
    @Autowired
    HttpUtils httpUtils;

    //GPT API地址，使用了代理服务器
    public static final String url = "https://mokjyz.xyz/v1/chat/completions";
    //GPTAPI Image
    public static final String urlImage = "https://mokjyz.xyz/v1/images/generations";
    // GPT API TOKEN
    public static final String token = "sk-*" ;

    // 私有构造函数
    private HttpSseUtils() {}

    //HTTP客户端库，可用于向Web服务器发起HTTP请求并处理响应，单例复用实例。
    private static class SingletonHolder {
        private static final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3,TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).pingInterval(60,TimeUnit.MINUTES)
                .build();
    }

    // 获取单例实例
    public static OkHttpClient getClient() {
        return HttpSseUtils.SingletonHolder.client;
    }


    //静态媒体类型，用来描述请求和响应消息的格式和字符集。
    private static final MediaType JSON = MediaType.get("application/json;chartset=utf-8");


    public void postStream(String url, JSONObject jsonObject , String apiKey, user_send user){
        // 定义see接口
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
               // .addHeader("Accept","text/event-stream")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+apiKey)
                .post(body)
                .build();
        EventSource.Factory factory = EventSources.createFactory(HttpSseUtils.getClient());
        EventSourceListener eventSourceListener = new EventSourceListener(){
            @Override
            public void onOpen(final EventSource eventSource, final Response
                    response) {
                System.out.println("建立sse连接...");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onEvent(final EventSource eventSource, final String
                    id, final String type, final String data) {
                System.out.println(id+":"+ data);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onClosed(final EventSource eventSource) {
                System.out.println("sse连接关闭...");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(final EventSource eventSource, final
            Throwable t, final Response response) {
                System.out.println("使用事件源时出现异常... "+response+t);
            }
        };
        factory.newEventSource(request, eventSourceListener);
    }



}
