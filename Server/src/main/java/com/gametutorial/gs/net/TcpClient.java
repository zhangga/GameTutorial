/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP客户端
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/2 15:18
 */
public class TcpClient {

    public static final int DATA_MAX_FRAME_LEN = 128 * 1024;
    public static final int DATA_FIELD_OFFSET_LEN = 2;
    public static final int DATA_FIELD_LEN_LEN = 4;
    public static final int DATA_ADJUSTMENT_LEN = 0;
    public static final int DATA_INITIAL_BYTES_TO_STRIP = 0;

    private String ip;
    private int port;
    private IActor actor;

    private EventLoopGroup boss;
    private ChannelFuture channel;

    public TcpClient(String ip, int port, IActor actor) {
        this.ip = ip;
        this.port = port;
        this.actor = actor;
    }

    public void connect() {
        try {
            boss = new NioEventLoopGroup(1);
            Bootstrap b = new Bootstrap();
            b.group(boss);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.SO_SNDBUF, 1024000);
            b.option(ChannelOption.SO_RCVBUF, 1024000);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.handler(new LoggingHandler(LogLevel.DEBUG));
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new IdleStateHandler(0, 0, 60));
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(DATA_MAX_FRAME_LEN, DATA_FIELD_OFFSET_LEN, DATA_FIELD_LEN_LEN,
                            DATA_ADJUSTMENT_LEN, DATA_INITIAL_BYTES_TO_STRIP, true));
                    ch.pipeline().addLast(new ClientChannelHandler(actor));
                }
            });
            channel = b.connect(ip, port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (channel != null) {
            channel.channel().close();
        }
        if (boss != null) {
            boss.shutdownGracefully();
        }
    }

}