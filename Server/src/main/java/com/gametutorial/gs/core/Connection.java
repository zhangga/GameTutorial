/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.gametutorial.gs.constant.ThreadConstant;
import io.netty.channel.Channel;

/**
 * 客户端连接
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 16:15
 */
@GService(threadName = ThreadConstant.LOGIC, prefix = "conn")
public class Connection extends GameService {

    private String id;

    private Channel channel;

    private ConnectionStatus status;

    public Connection() {

    }

    public Connection(GameThread thread, int id, Channel channel) {
        super(thread, id);
        this.channel = channel;
        this.status = ConnectionStatus.LOGIN;
    }

    @Override
    public String getId() {
        return id;
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