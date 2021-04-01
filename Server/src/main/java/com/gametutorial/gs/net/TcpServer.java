/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.net;

import com.gametutorial.gs.config.GameConf;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty启动的tcp服务
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 13:38
 */
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static final int DATA_MAX_FRAME_LEN = 128 * 1024;
    public static final int DATA_FIELD_OFFSET_LEN = 2;
    public static final int DATA_FIELD_LEN_LEN = 4;
    public static final int DATA_ADJUSTMENT_LEN = 0;
    public static final int DATA_INITIAL_BYTES_TO_STRIP = 0;

    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ChannelFuture channel;

    private GameConf gameConf;

    public TcpServer(GameConf gameConf) {
        this.gameConf = gameConf;
    }

    public void start() {
        try {
            boss = new NioEventLoopGroup(gameConf.getServerConf().getBossThread());
            worker = new NioEventLoopGroup(gameConf.getServerConf().getWorkerThread());

            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_REUSEADDR, true);
            b.option(ChannelOption.SO_BACKLOG, 10240);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);// 使用内存池

            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.SO_SNDBUF, 1024000);
            b.childOption(ChannelOption.SO_RCVBUF, 1024000);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);// 使用内存池
            b.handler(new LoggingHandler(LogLevel.DEBUG));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(
                            new IdleStateHandler(0, 0,
                                    gameConf.getServerConf().getIdleTimeoutSec()),
                            new LengthFieldBasedFrameDecoder(DATA_MAX_FRAME_LEN, DATA_FIELD_OFFSET_LEN, DATA_FIELD_LEN_LEN,
                                    DATA_ADJUSTMENT_LEN, DATA_INITIAL_BYTES_TO_STRIP, true),
                            new ServerChannelHandler()
                    );
                }
            });
            channel = b.bind(gameConf.getServerConf().getPort()).sync();
            channel.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("start tcp server error={}", e);
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void stop() {
        if (channel != null) {
            channel.channel().close();
        }
        if (worker != null) {
            worker.shutdownGracefully();
        }
        if (boss != null) {
            boss.shutdownGracefully();
        }
    }

}