package com.cuit.controller;

import cn.hutool.http.server.HttpServerBase;
import com.cuit.service.ChatService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("hello")
public class HelloController {

    @Autowired
    private ChatService chatService;
    @GetMapping("world")
    public String hello(){
        return "qiweikai";
    }
    @GetMapping("chat")
    public String chat(String msg){
        return chatService.chatTest(msg);
    }

    @GetMapping("chat/response")
    public Flux<ChatResponse> chatResponse(String msg){
        return chatService.streamResponse(msg);
    }


    @GetMapping("chat/stream/str")
    public Flux<String> chatStreaStr(HttpServletResponse response,String msg){
        response.setContentType("text/html;charset=utf-8");
        return chatService.streamStr(msg);
    }
}
