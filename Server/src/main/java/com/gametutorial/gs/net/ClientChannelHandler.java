/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ChannelHandler
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/2 15:23
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private IActor actor;

    public ClientChannelHandler(IActor actor) {
        this.actor = actor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        actor.setChannel(ctx.channel());
        actor.onConnected();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        actor.onDisconnect();
    }
}