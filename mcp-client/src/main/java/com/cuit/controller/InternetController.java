package com.cuit.controller;

import com.cuit.service.ChatService;
import com.cuit.service.SearXngService;
import com.cuit.vo.ChatVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internet")
public class InternetController {
    @Autowired
    private SearXngService searXngService;

    @Autowired
    private ChatService chatService;
    @GetMapping("/test")
    public Object test(@RequestParam("query") String query){
        return searXngService.search(query);
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatVo chatVo, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        chatService.doInternetSearch(chatVo);

    }

}
