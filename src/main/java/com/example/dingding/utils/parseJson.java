package com.example.dingding.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class parseJson {
    //Jackson库中的一个核心类，它提供了序列化和反序列化JSON的功能.
    private static final ObjectMapper objectMapper=new ObjectMapper();

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

    /**
     * 用于将Java对象序列化成JSON字符串。
     * @param obj 要序列化的Java对象。
     * @return JSON字符串。
     * @throws JsonProcessingException 如果序列化失败。
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

}
