package com.cuit.utils;

import com.cuit.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    //存放所有用户
    private static final Map<String,SseEmitter> sseClients = new ConcurrentHashMap<>();

    public static SseEmitter conncet(String userId){
        //设置超时时间，0l表示不超时，默认是30秒，超时未完成则会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);

        //注册回调方法
        sseEmitter.onTimeout(timeoutCallback(userId));//超时结束
        sseEmitter.onError(errorCallback(userId));//移除结束
        sseEmitter.onCompletion(completionCallback(userId));//正常结束
        sseClients.put(userId,sseEmitter);
        log.info("SSE连接创建成功,连接的用户id为:{}",userId);
        return sseEmitter;
    }
    public static Runnable timeoutCallback(String userId){
        return () -> {
            log.info("SSE超时...");
            //删除用户
            remove(userId);
        };
    }
    public static Runnable completionCallback(String userId){
        return () -> {
            log.info("SSE正常结束...");
            //删除用户
            remove(userId);
        };
    }
    public static Consumer<Throwable> errorCallback(String userId){
        return (throwable) -> {
            log.error("SSE异常...");
            remove(userId);
        };
    }
    public static void remove(String userId){
        sseClients.remove(userId);
        log.info("SSE连接被移除，移除的用户id为:{}",userId);
    }
    //发送一条
    public static void sendMsg(String userId,String message,SSEMsgType msgType){
        if(CollectionUtils.isEmpty(sseClients)){
            return;
        }
        if(sseClients.containsKey(userId)){
            SseEmitter sseEmitter = sseClients.get(userId);
            sendEmitterMessage(sseEmitter,userId,message,msgType);
        }
    }
    //发送多条
    public static void sendMsgToAllUsers(String message){
        if(CollectionUtils.isEmpty(sseClients)){
            return;
        }
        sseClients.forEach((userId,sseEmitter)->{
            sendEmitterMessage(sseEmitter,userId,message,SSEMsgType.MESSAGE);
        });
    }

    private static void sendEmitterMessage(SseEmitter sseEmitter,
                                           String userId,String message,
                                           SSEMsgType sseMsgType){
        try {
            SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                    .id(userId)
                    .data(message).
                    name(sseMsgType.type);
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            log.error("SSE异常......{}",e.getMessage());
            remove(userId);
        }
    }
}
