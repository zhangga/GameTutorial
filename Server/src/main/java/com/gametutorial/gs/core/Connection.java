/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.gametutorial.gs.constant.ThreadConstant;
import com.gametutorial.gs.service.AccountService;
import com.gametutorial.gs.service.PlayerService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 客户端连接
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 16:15
 */
@GService(threadName = ThreadConstant.CONNECT, prefix = "conn")
public class Connection extends GameService {

    /** 连接Channel */
    private Channel channel;

    /** 连接状态 */
    private ConnectionStatus status;

    /** 待处理数据 */
    private final LinkedBlockingQueue<ByteBuf> input = new LinkedBlockingQueue<>();

    public Connection() {

    }

    public Connection(GameThread thread, int id, Channel channel) {
        super(thread, id);
        this.channel = channel;
        this.status = ConnectionStatus.LOGIN;
    }

    /**
     * 添加协议
     * @param buf
     */
    public void onMsgInput(ByteBuf buf) {
        try {
            input.put(buf);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pulseOverride() {
        // 分发协议
        dispatchInput();
    }

    /**
     * 分发协议
     * Connection模拟网关服，不在这解析协议
     */
    private void dispatchInput() {
        while (!input.isEmpty()) {
            ByteBuf msg = input.poll();
            switch (status) {
                // 登录阶段
                case LOGIN:
                    AccountService accountProxy = GameServiceFactory.createProxy(AccountService.class);
                    accountProxy.msgHandler(msg);
                    break;
                // 游戏中
                case GAME:
                    PlayerService playerProxy = GameServiceFactory.createProxy(PlayerService.class);
                    playerProxy.msgHandler(msg);
                    break;
                case CLOSED:
                    break;
            }
        }
    }

    /**
     * 玩家连接状态
     */
    private static enum ConnectionStatus {
        // 登录中
        LOGIN,
        // 游戏中
        GAME,
        // 已断开
        CLOSED,
    }
}