/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TcpActor
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/2 15:48
 */
public abstract class TcpActor implements IActor {

    private static final Logger log = LoggerFactory.getLogger(TcpActor.class);

    private Channel channel;

    private Map<Class<? extends Message>, Integer> msgCmd = new ConcurrentHashMap<>();

    @Override
    public void setChannel(Channel ch) {
        this.channel = ch;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReceive() {

    }

    @Override
    public void sendMsg(GeneratedMessageV3.Builder builder) {
        Message msg = builder.build();
        // 获取cmd
        Class<? extends Message> msgClazz = msg.getClass();
        int cmd = msgCmd.getOrDefault(msgClazz, 0);
        if (cmd == 0) {
            try {
                Method descMethod = msg.getClass().getMethod("getDescriptor");
                Descriptors.Descriptor descriptor = (Descriptors.Descriptor) descMethod.invoke(msgClazz);
                for (var entry : descriptor.getOptions().getAllFields().entrySet()) {
                    var key = entry.getKey();
                    if (key.getJsonName().equals("cmd")) {
                        cmd = (int) entry.getValue();
                        break;
                    }
                }
                if (cmd == 0) {
                    log.error("msg={}, cmd is 0", msgClazz.getName());
                    return;
                }
                msgCmd.put(msgClazz, cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] msgBody = msg.toByteArray();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(6 + msgBody.length);
        // cmd
        buf.writeShort(cmd);
        // body length
        buf.writeInt(msgBody.length);
        // body
        buf.writeBytes(msgBody);
        channel.writeAndFlush(buf);
        log.info("send msg -> \n{}", msg);
    }
}