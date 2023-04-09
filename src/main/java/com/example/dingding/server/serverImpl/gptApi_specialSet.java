package com.example.dingding.server.serverImpl;

import com.example.dingding.pojo.user_send;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class gptApi_specialSet {

    @Autowired
    HttpUtils http;
    @Autowired
    gptApi gptApi;

    /**
     * 预设API接口，Image相关
     * @param user 用户对象，存储了用户的信息
     * @return  API返回的json字符串
     * @throws IOException
     */
    public String gptApiImageSet(user_send user) throws IOException {
        //chatGPT system预设语句
        String SetApi="从现在开始，你是一名中英翻译，你会根据我输入的中文内容，添加修饰词润色并翻译成对应英文。请注意，你翻译后的内容主要服务于一个绘画AI Dall-e，它只能理解具象的描述而非抽象的概念，同时根据你对绘画AI的理解，比如它可能的训练模型、自然语言处理方式等方面，进行翻译优化。由于我的描述可能会很散乱，不连贯，你需要综合考虑这些问题，然后对翻译后的英文内容再次优化或重组，从而使绘画AI更能清楚我在说什么，并适当添加高质量、高分辨率、高细节的修饰词。请严格按照此条规则进行翻译，也只输出翻译后的英文内容。 例如，我输入：一只想家的小狗。\n" +
                "你不能输出：\n"  +
                "A homesick little dog.\n" +
                "你必须输出：\n" +
                "A high quality photo of A small dog that misses home, with a sad look on its face and its tail tucked between its legs. It might be standing in front of a closed door or a gate, gazing longingly into the distance, as if hoping to catch a glimpse of its beloved home.\n" +
                "如果你明白了，当我输入中文内容后，请翻译我需要的英文内容。";
        //用户描述
        String content = gptApi.cleanContent(user.getContent());

        //添加JSON
        JSONObject requestBody = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        JSONObject system = new JSONObject();
        //添加GPT system预设
        system.put("role","system");
        system.put("content",SetApi);
        //message 添加用户问题
        message.put("role","user");
        message.put("content",content);
        //messageArray
        messagesArray.put(system);
        messagesArray.put(message);
        //JsonObject 设置GPT参数
        requestBody.put("messages", messagesArray);
        requestBody.put("temperature", 1);
        requestBody.put("model", "gpt-3.5-turbo");
        //执行
        return http.post(http.url,requestBody,http.token,user);
    }
}
