package com.example.mcpserver;

import com.example.mcpserver.tool.DateTool;
import com.example.mcpserver.tool.EmailTool;
import com.example.mcpserver.tool.ProductTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;


@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
    @Bean
    public ToolCallbackProvider toolCallbackPrivider(DateTool dateTool, EmailTool emailTool, ProductTool productTool){
        // 写错了
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(dateTool,emailTool,productTool)
                .build();
    }
}
