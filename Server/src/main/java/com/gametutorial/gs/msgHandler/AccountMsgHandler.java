/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.msgHandler;

import com.gametutorial.gs.core.MsgHandler;
import com.gametutorial.gs.core.MsgReceiver;
import com.gametutorial.gs.msg.MsgGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录阶段消息处理
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 21:50
 */
@MsgHandler
public class AccountMsgHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountMsgHandler.class);

    @MsgReceiver(MsgGame.C2S_Login.class)
    public static void login(MsgGame.C2S_Login msg) {
        log.info("login {}", msg);
        // 登录逻辑

        // 修改connection对象

    }

}