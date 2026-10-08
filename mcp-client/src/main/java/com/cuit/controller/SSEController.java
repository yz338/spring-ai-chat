package com.cuit.controller;

import com.cuit.enums.SSEMsgType;
import com.cuit.utils.SSEServer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("sse")
public class SSEController {
    /*
    TEXT_EVENT_STREAM_VALUE
    告诉浏览器：
        1.这不是一个普通的文本或者是json，这是一个sse事件流
        2.不要关闭连接，保持监听状态，等待我，持续发送数据
        3.解析规则：请按照sse的标准格式(比如以data:开头,以\n\n结尾)来解析我们发过去的数据
     */
    @GetMapping(path = "connect",produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@RequestParam String userId){
        return SSEServer.conncet(userId);
    }

    //sse发送单个消息
    @GetMapping("sendMessage")
    public Object sendMessage(@RequestParam String userId,@RequestParam String message){
        SSEServer.sendMsg(userId,message, SSEMsgType.MESSAGE);
        return "OK";
    }

    //SSE发送群消息
    @GetMapping("sendMessageAll")
    public Object sendMessageAll(@RequestParam String message){
        SSEServer.sendMsgToAllUsers(message);
        return "OK";
    }


    //发送单个消息 add
    @GetMapping("sendMessageAdd")
    public Object sendMessageAdd(@RequestParam String userId,@RequestParam String message) throws InterruptedException {
        for(int i=0;i<10;i++){
            Thread.sleep(200);
            SSEServer.sendMsg(userId,message, SSEMsgType.ADD);
        }
        return "OK";
    }
}
