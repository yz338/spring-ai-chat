package com.cuit.service;

import com.cuit.enums.SSEMsgType;
import com.cuit.utils.SSEServer;
import com.cuit.vo.ChatVo;
import com.cuit.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private SearXngService searXngService;

    private ChatClient chatClient;

    private ChatMemory chatMemory;

    private String systemPrompt=
            """
                你是一个非常聪明的人工智能助手，可以帮我解决很多问题我为你取一个名字，你得名字叫LaGoGo
            """;

    //构造器注入
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClientBuilder.defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    @Override
    public String chatTest(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public Flux<ChatResponse> streamResponse(String prompt) {
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    @Override
    public Flux<String> streamStr(String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public void doChat(ChatVo chatVo) {
        String userId = chatVo.getCurrentUserName();
        String prompt = chatVo.getMessage();
        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        stringFlux.toStream().map(chatResponse->{
            String content = chatResponse.toString();
            log.info("content:{}",content);
            SSEServer.sendMsg(userId,content, SSEMsgType.ADD);
            return content;
        }).collect(Collectors.toList());
    }

    private static final String ragPROMPT= """
                        基于上下文的知识库内容回答问题
                        [上下文]
                        {content}
                        
                        问题
                        {question}
                        
                        [输出]
                        如果没有查到，请回复：不知道
                        如果查询，请回复具体的内容，不相关的近似内容不必提到
            """;

    @Override
    public void doChatRagSearch(ChatVo chatVo, List<Document> ragContext) {
        String userId = chatVo.getCurrentUserName();
        String question = chatVo.getMessage();
        //构建提示词
        String context=null;
        if(ragContext!=null && ragContext.size()>0){
            context=ragContext.stream().map(Document::getText).
                    collect(Collectors.joining("\n"));
        }
        //组装提示词
        Prompt prompt=new Prompt(ragPROMPT
                .replace("{content}",context)
                .replace("{question}",question));
        log.info("<prompt>提示词内容如下{}",prompt.toString());
        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        stringFlux.toStream().map(chatResponse->{
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId,content, SSEMsgType.ADD);
            return content;
        }).collect(Collectors.toList());
    }
    // 智能体搜索引擎的提示词
    private static final String searXmgPrompot= """
                                        你是一个互联网搜索大师，请基于以下互联网返回的结果作为上下文，根据你的理解综合后，生成并且输出专业回答:
                                        [上下文]
                                        {content}
                                            
                                        [问题]
                                        {question}
                           
                                        [输出]
                                        如果没有查到，请回复：不知道
                                        如果查到，请回复具体的内容，不相关的内容不必提到。
                                            
                                         """;
    @Override
    public void doInternetSearch(ChatVo chatVo) {
        String userId = chatVo.getCurrentUserName();
        String question = chatVo.getMessage();

        List<SearchResult> searchResults = searXngService.search(question);
        String finalPrompt = buildSearXngPrompt(question, searchResults);
        Prompt prompt=new Prompt(finalPrompt);
        log.info("<prompt>提示词内容如下{}",prompt.toString());
        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        stringFlux.toStream().map(chatResponse->{
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId,content, SSEMsgType.ADD);
            return content;
        }).collect(Collectors.toList());
    }

    private static String buildSearXngPrompt(String question,List<SearchResult> searchResults){
        StringBuilder context=new StringBuilder();
        searchResults.forEach(searchResult->{
            context.append(String.format("<context>\n[来源] %s \n [摘要] %s \n </context>\n",
                    searchResult.getUrl(),
                    searchResult.getContent()));
        });
        return searXmgPrompot.replace("{content}",context)
                .replace("{question}",question);
    }
}
