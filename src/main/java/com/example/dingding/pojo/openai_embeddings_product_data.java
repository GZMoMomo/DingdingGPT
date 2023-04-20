package com.example.dingding.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class openai_embeddings_product_data implements Serializable {
    //文本索引id
    int id;
    //产品名称
    String product;
    //文本小标题
    String title;
    //文本内容
    String content;
    //向量数据
    String ada_002;
    //描述消耗的tokens
    int prompt_tokens;
    //总消耗tokens
    int total_tokens;
}
