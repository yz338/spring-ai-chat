package com.example.mcpserver.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Email {
    @ToolParam(description = "收件人")
    private String email;
    @ToolParam(description = "邮件标题")
    private String subject;
    @ToolParam(description = "邮件内容")
    private String message;
}
