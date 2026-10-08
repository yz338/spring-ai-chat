package com.cuit.controller;

import com.cuit.service.ChatService;
import com.cuit.vo.ChatVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("doChat")
    public void chat(@RequestBody ChatVo chatVo){
        chatService.doChat(chatVo);
    }
}
