/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import com.gametutorial.gs.Server;
import com.gametutorial.gs.core.Connection;
import com.gametutorial.gs.core.GameServiceFactory;
import com.gametutorial.gs.core.GameThread;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

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
        Channel channel = ctx.channel();
        // 分配一个随机线程
        int connId = idAuto.incrementAndGet();
        GameThread thread = Server.getThread(GameServiceFactory.getGService(Connection.class).threadName(), connId);
        conn = new Connection(thread, connId, channel);
        conn.startup();

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
        ByteBuf buf = (ByteBuf) msg;
        buf.markReaderIndex();
        // cmd
        buf.readShort();
        int length = buf.readInt();
        if (buf.readableBytes() < length) {
            buf.resetReaderIndex();
            return;
        }
        buf.resetReaderIndex();
        conn.onMsgInput(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 断连的日志就不打印堆栈了
        if (cause.getMessage().contains("Connection reset by peer") ||
                cause.getMessage().startsWith("远程主机强迫关闭了一个现有的连接") ||
                cause.getMessage().startsWith("杩滅▼涓绘満寮鸿揩鍏抽棴")) {
            log.debug("{}", cause);
        } else {
            InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIP = socket.getAddress().getHostAddress();
            if (clientIP != null) {
                log.error("非法的请求：ip={}, port={}", clientIP, socket.getPort());
            }
            //输出错误日志
            log.error("发生异常：connId={}, cause={}", conn.getId(), cause);
            cause.printStackTrace();
        }
    }
}