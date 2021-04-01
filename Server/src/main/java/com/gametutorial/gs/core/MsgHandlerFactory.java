/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息分发处理
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 21:17
 */
public class MsgHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(MsgHandlerFactory.class);

    private static final String CMD_KEY = "cmd";

    /** 消息号对应的类 */
    private static final Map<Integer, Class<? extends GeneratedMessageV3>> msgClass = new HashMap<>();
    private static final Map<Integer, Method> msgParser = new HashMap<>();
    private static final Map<Integer, Method> msgReceiver = new HashMap<>();

    /**
     * 处理MsgReceiver注解
     * @param clazz
     */
    public static void init(Class<?> clazz) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(clazz);
        String[] beans = context.getBeanNamesForAnnotation(MsgHandler.class);
        for (String beanName : beans) {
            Object bean = context.getBean(beanName);
            for (Method method : bean.getClass().getDeclaredMethods()) {
                MsgReceiver mr = method.getAnnotation(MsgReceiver.class);
                if (mr == null)
                    continue;

                Class<? extends GeneratedMessageV3> msgClazz = mr.value();
                try {
                    int cmd = 0;
                    Method descMethod = msgClazz.getMethod("getDescriptor");
                    Descriptors.Descriptor descriptor = (Descriptors.Descriptor) descMethod.invoke(msgClazz);
                    for (var entry : descriptor.getOptions().getAllFields().entrySet()) {
                        var key = entry.getKey();
                        if (key.getJsonName().equals(CMD_KEY)) {
                            cmd = (int) entry.getValue();
                            break;
                        }
                    }
                    // 消息号必须大于0
                    if (cmd <= 0) {
                        String exp = "MsgClass=" + msgClazz.getName() + ", has not 【option (cmd) = ?;】";
                        log.error(exp);
                        throw new Exception(exp);
                    }
                    // 消息对应的类
                    if (msgClass.containsKey(cmd)) {
                        String exp = "cmd="+cmd+" duplicate, MsgClass=" + msgClazz.getName() + " & " + msgClass.get(cmd).getName();
                        log.error(exp);
                        throw new Exception(exp);
                    }
                    msgClass.put(cmd, msgClazz);
                    // 消息解析器
                    Method parser = msgClazz.getMethod("parseFrom", CodedInputStream.class);
                    msgParser.put(cmd, parser);
                    // 消息处理器
                    if (msgReceiver.containsKey(cmd)) {
                        String exp = "cmd="+cmd+" duplicate, Method=" + method.getName() + " & " + msgReceiver.get(cmd).getName();
                        log.error(exp);
                        throw new Exception(exp);
                    }
                    msgReceiver.put(cmd, method);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析协议
     * @param msg
     */
    public static void decode(ByteBuf msg) {
        int cmd = msg.readShort();
        Method parser = msgParser.get(cmd);
        if (parser == null) {
            log.error("cmd={} has not MsgParser", cmd);
            return;
        }
        // msg length
        int length = msg.readInt();
        byte[] body = new byte[length];
        msg.readBytes(body);
        CodedInputStream in = CodedInputStream.newInstance(body);
        Object request = null;
        try {
            request = parser.invoke(null, in);
        } catch (Exception e) {
            log.error("decode msg cmd={}, class={}, error={}", cmd, msgClass.get(cmd).getName(), e);
            e.printStackTrace();
        }
        Method receiver = msgReceiver.get(cmd);
        if (receiver == null) {
            log.error("cmd={} has not MsgReceiver", cmd);
            return;
        }
        try {
            receiver.invoke(null, request);
        } catch (Exception e) {
            log.error("handle msg cmd={}, MsgReceiver={}, error={}", cmd, receiver.getName(), e);
            e.printStackTrace();
        }
    }

}