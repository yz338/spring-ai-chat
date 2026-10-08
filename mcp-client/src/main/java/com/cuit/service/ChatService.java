package com.cuit.service;

import com.cuit.vo.ChatVo;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    //测试发模型交互聊天
    public String chatTest(String prompt);

    //测试大模型流式交互聊天 返回json
    public Flux<ChatResponse> streamResponse(String prompt);

    //测试大模型流式交互聊天(流式响应string)
    public Flux<String> streamStr(String prompt);


    //和项目大模型进行交互
    public void doChat(ChatVo chatVo);

    //RAG 知识库检索汇总给大模型输出
    public void doChatRagSearch(ChatVo chatVo, List<Document> ragContext);

    //联网搜索
    public void doInternetSearch(ChatVo chatVo);
}
