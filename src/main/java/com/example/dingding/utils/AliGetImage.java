package com.example.dingding.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.tea.TeaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliGetImage {

    String appKey="dingqqd5gm3ela9sevkb";
    String appSecret="YuXFetoMVAhsuZryrQGLz3kgQEUQ7871wtAQMmjOds2FDMnbUt24IDQZypCy-PgH";
    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkrobot_1_0.Client createClient_010() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkrobot_1_0.Client(config);
    }

    public static com.aliyun.dingtalkoauth2_1_0.Client createClient_210() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    public String getAliImageJson(JSONObject jsonObject){
        //content=(jsonObject.getJSONObject("text").get("content").toString().replaceAll(" ",""));
        return "";
    }

    private String getAliImageUrl(String downloadCode,String robotCode) throws Exception {
        com.aliyun.dingtalkrobot_1_0.Client client = AliGetImage.createClient_010();
        com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadHeaders robotMessageFileDownloadHeaders = new com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadHeaders();
        robotMessageFileDownloadHeaders.xAcsDingtalkAccessToken = getAccessToken(appKey,appSecret);
        com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadRequest robotMessageFileDownloadRequest = new com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadRequest()
                .setDownloadCode(downloadCode)
                .setRobotCode(robotCode);
        String imageUrl=client.robotMessageFileDownloadWithOptions(robotMessageFileDownloadRequest, robotMessageFileDownloadHeaders, new com.aliyun.teautil.models.RuntimeOptions()).getBody().getDownloadUrl();
        return imageUrl;
    }


    private String getAccessToken(String appKey,String appSecret) throws Exception {
        com.aliyun.dingtalkoauth2_1_0.Client client = AliGetImage.createClient_210();
        com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                .setAppKey(appKey)
                .setAppSecret(appSecret);
        String response= client.getAccessToken(getAccessTokenRequest).getBody().getAccessToken();
        return response;


    }


}
