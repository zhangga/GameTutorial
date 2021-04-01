/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.robot;

import com.gametutorial.gs.msg.MsgGame;
import com.gametutorial.gs.net.TcpActor;
import com.gametutorial.gs.net.TcpClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 机器人
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/2 15:15
 */
public class Robot extends TcpActor {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private TcpClient client;

    public static void main(String[] args) {
        Robot robot = new Robot();
        robot.start();
    }

    public void start() {
        client = new TcpClient("127.0.0.1", 10000, this);
        client.connect();
    }

    @Override
    public void onConnected() {
        scheduler.schedule(() -> {
            MsgGame.C2S_Login.Builder builder = MsgGame.C2S_Login.newBuilder();
            builder.setName("zzq");
            builder.setEmail("123@qq.com");
            this.sendMsg(builder);
        }, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReceive() {

    }

}