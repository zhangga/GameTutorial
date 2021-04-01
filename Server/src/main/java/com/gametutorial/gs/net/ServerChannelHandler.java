/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import com.gametutorial.gs.Server;
import com.gametutorial.gs.core.Connection;
import com.gametutorial.gs.core.GameServiceFactory;
import com.gametutorial.gs.core.GameThread;
import com.gametutorial.gs.service.PlayerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * ChannelHandler
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 14:28
 */
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ServerChannelHandler.class);

    private static final AtomicInteger idAuto = new AtomicInteger();

    /** 当前连接信息 */
    private Connection conn;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        // 分配一个随机线程
        int connId = idAuto.incrementAndGet();
        GameThread thread = Server.getThread(GameServiceFactory.getGService(Connection.class).threadName(), connId);
        conn = new Connection(thread, connId, channel);

        // 日志
        InetSocketAddress addrLocal = (InetSocketAddress) channel.localAddress();
        log.info("新建一个连接：连接ID={}, 所属线程={}, 本地端口={}, 远程地址={}", conn.getId(), thread.getId(), addrLocal.getPort(), channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        PlayerService player = GameServiceFactory.createProxy(PlayerService.class, 1);
        player.login().subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {

            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}