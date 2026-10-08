package com.example.mcpserver.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateTool {

    @Tool(description = "获取本机当前时间")
    public String getCurrentTime() {
        log.info("调用mcp工具");
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("获取当前时间: {}", time);
        return time;
    }
    @Tool(description = "根据时区获取当前时间")
    public String getCurrentTimeByZone(
            @ToolParam(description = "时区ID，例如 Asia/Shanghai") String zone,
            @ToolParam(description = "城市名称，例如 上海") String cityName) {
        log.info("调用mcp工具，参数zone：{}，cityName：{}", zone, cityName);
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);  // ← 关键改动
        String time = String.format("%s当前时间是%s", cityName,
                zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("获取当前时间: {}", time);
        return time;
    }
}