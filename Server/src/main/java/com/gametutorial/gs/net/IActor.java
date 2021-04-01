/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import io.netty.channel.Channel;
import com.google.protobuf.GeneratedMessageV3.Builder;

/**
 * IActor
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/2 15:34
 */
public interface IActor {

    void setChannel(Channel ch);

    Channel getChannel();

    void onConnected();

    void onDisconnect();

    void onReceive();

    /**
     * 发送protobuf协议
     * @param builder
     */
    void sendMsg(Builder builder);

}