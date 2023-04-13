package com.example.dingding.server;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.dingding.pojo.user_send;
import com.taobao.api.ApiException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.example.dingding.server.serverImpl.gptApi;
import com.example.dingding.mapper.user_sendMapper;

@Service
public class sendMsg {
    @Autowired
    gptApi gptApi;
    @Autowired
    user_sendMapper user_sendMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     *  获取已经存储信息的user_send对象输出答案到钉钉，并将信息缓存到redis中
     * @throws IOException
     */
    public void sendMsg(user_send user)  {
        //将GPT API的回答发送至钉钉
        text(user);
        //将事务存储在mysql
        user.setGptApiType("chat");
        user_sendMapper.insert(user);
        //将聊天记录存储在redis，十分钟后删除
        redisTemplate.opsForValue().set(user.getSenderStaffId()+"_"+user.getCreateAt(),user,600, TimeUnit.SECONDS);
    }

    /**
     *  获取已经存储信息的user_send对象输出答案到钉钉，并将信息缓存到redis中
     * @throws IOException
     */
    public void sendImageUrl(user_send user) throws IOException {
        //将GPT API的回答发送至钉钉
        image(user);
        user.setGptApiType("createImage");
        user.setPrompt_tokens(null);
        user.setCompletion_tokens(null);
        user.setTotal_tokens(null);
        //将事务存储在mysql
        user_sendMapper.insert(user);
    }

    /**
     * 发送信息
     * @param user user_send对象，存储了用户的问题、答案等等信息
     */
    private void text(user_send user){
        try {
            //创建钉钉客户端
            DingTalkClient client=new DefaultDingTalkClient(user.getSessionWebhook());
            //创建发送至钉钉的request
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            //发送至钉钉的消息类型为markdown
            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            //设置Markdown标题（暂不生效）
            markdown.setTitle("答案生成完毕！");
            //获取Image API返回的图片url
            String answer=user.getAnswer();
            //发送信息内容并@用户
            markdown.setText(" @" + user.getSenderStaffId() + "  \n  " + answer);
            //设置request
            request.setMarkdown(markdown);
            //@用户设置
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtUserIds(Arrays.asList(user.getSenderStaffId()));
            //isAtAll类型如果不为Boolean，请升级至最新SDK
            at.setIsAtAll(false);
            request.setAt(at);
            //执行
            OapiRobotSendResponse response = client.execute(request);
        }catch (ApiException e){
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     * @param user user_send对象，存储了用户的问题、答案等等信息
     */
    private void image(user_send user){
        try {
            //创建钉钉客户端
            DingTalkClient client=new DefaultDingTalkClient(user.getSessionWebhook());
            //创建发送至钉钉的request
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            //发送至钉钉的消息类型为markdown
            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            //设置Markdown标题（暂不生效）
            markdown.setTitle("图像生成完毕！");
            //获取Image API返回的图片url
            String answer=user.getAnswer();
            JSONArray jsonArray=new JSONArray(answer);
            StringBuffer sb=new StringBuffer();
            //循环遍历添加多个图片URL
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String url=jsonObject.getString("url");
                sb.append("![]("+url+")\n");
            }
            //发送信息内容并@用户
            markdown.setText(" @" + user.getSenderStaffId() + "  \n  " + sb);
            //设置request
            request.setMarkdown(markdown);
            //@用户设置
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtUserIds(Arrays.asList(user.getSenderStaffId()));
            //isAtAll类型如果不为Boolean，请升级至最新SDK
            at.setIsAtAll(false);
            request.setAt(at);
            //执行
            OapiRobotSendResponse response = client.execute(request);
        }catch (ApiException e){
            e.printStackTrace();
        }
    }

    /**
     * 自定义发送信息发送信息
     * @param user user_send对象，存储了用户的问题、答案等等信息
     * @param content 自定义信息
     */
    public void freeText(user_send user,String content){
        try {
            //创建钉钉客户端
            DingTalkClient client=new DefaultDingTalkClient(user.getSessionWebhook());
            //获取用户ID
            String senderstaffid=user.getSenderStaffId();
            //创建发送至钉钉的request
            OapiRobotSendRequest request=new OapiRobotSendRequest();
            //发送至钉钉的消息类型
            request.setMsgtype("text");
            //发送至钉钉的信息内容
            OapiRobotSendRequest.Text text=new OapiRobotSendRequest.Text();
            text.setContent("@"+senderstaffid+"\n"+content);
            request.setText(text);
            //设置钉钉的@功能 @信息发送者
            OapiRobotSendRequest.At at=new OapiRobotSendRequest.At();
            at.setAtUserIds(Arrays.asList(senderstaffid));
            //是否@所有人 否
            at.setIsAtAll(false);
            request.setAt(at);
            //执行request
            OapiRobotSendResponse response=client.execute(request);
        }catch (ApiException e){
            e.printStackTrace();
        }
    }

    /**
     * markdown发送信息
     * @param user user_send对象，存储了用户的问题、答案等等信息
     */
    private void markDownText(user_send user){
        try {
            //创建钉钉客户端
            DingTalkClient client=new DefaultDingTalkClient(user.getSessionWebhook());
            //创建发送至钉钉的request
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            //发送至钉钉的消息类型为markdown
            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            //获取Image API返回的图片url
            String answer=user.getAnswer();
            //发送信息内容并@用户
            markdown.setText(" @" + user.getSenderStaffId() + "  \n  " + answer);
            //设置request
            request.setMarkdown(markdown);
            //@用户设置
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtUserIds(Arrays.asList(user.getSenderStaffId()));
            //isAtAll类型如果不为Boolean，请升级至最新SDK
            at.setIsAtAll(false);
            request.setAt(at);
            //执行
            OapiRobotSendResponse response = client.execute(request);
        }catch (ApiException e){
            e.printStackTrace();
        }
    }
}
