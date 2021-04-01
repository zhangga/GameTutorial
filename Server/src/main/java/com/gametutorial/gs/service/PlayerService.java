/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.service;

import com.gametutorial.gs.constant.ThreadConstant;
import com.gametutorial.gs.core.*;
import io.netty.buffer.ByteBuf;

/**
 * 玩家服务
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 16:22
 */
@GService(threadName = ThreadConstant.LOGIC)
public class PlayerService extends GameService {

    @GMethod
    public FlowResult<Void> msgHandler(ByteBuf msg) {
        MsgHandlerFactory.decode(msg);
        return null;
    }

}