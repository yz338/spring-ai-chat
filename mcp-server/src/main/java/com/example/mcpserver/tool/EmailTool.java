package com.example.mcpserver.tool;

import com.example.mcpserver.bean.Email;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;


@Slf4j
@Component
public class EmailTool {
    @Autowired
    private JavaMailSender sender;
    private final String from;

    public EmailTool(JavaMailSender sender,@Value("${spring.mail.username}") String from) {
        this.from = from;
        this.sender = sender;
    }

    @Tool(description = "给指定的邮箱发送消息，email是收件人，subject是邮箱标题，message是邮件内容")
    public void SendEmail(Email email) {
        try {
            log.info("调用mcp工具");
            log.info("收件人: {}, 邮件标题: {}, 邮件内容: {}", email.getEmail(), email.getSubject(), email.getMessage());
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(email.getEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage());
            sender.send(message);
        }catch (Exception e){
            log.error("发送邮件失败: {}", e.getMessage());
        }
    }
    @Tool(description = "给指定的邮箱发送HTML邮件，email是收件人，subject是邮件标题，message是HTML邮件内容")
    public String SendHtmlEmail(Email email) {
        try {
            log.info("调用mcp工具发送HTML邮件");
            log.info("收件人: {}, 邮件标题: {}", email.getEmail(), email.getSubject());

            // 将 Markdown 转为 HTML
            Parser parser = Parser.builder().build();
            Node document = parser.parse(email.getMessage());
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String htmlContent = renderer.render(document);

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(email.getEmail());
            helper.setSubject(email.getSubject());
            helper.setText(htmlContent, true);
            sender.send(message);
            return "HTML邮件发送成功";
        } catch (Exception e) {
            log.error("发送HTML邮件失败: {}", e.getMessage());
            return "HTML邮件发送失败: " + e.getMessage();
        }
    }
}
