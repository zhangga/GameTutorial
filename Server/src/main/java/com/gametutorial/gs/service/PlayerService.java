/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.service;

import com.gametutorial.gs.constant.ThreadConstant;
import com.gametutorial.gs.core.GService;
import com.gametutorial.gs.core.GameService;
import reactor.core.publisher.Mono;

/**
 * 玩家服务
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 16:22
 */
@GService(threadName = ThreadConstant.LOGIC)
public class PlayerService extends GameService {

    @Override
    public String getId() {
        return null;
    }

    public Mono<Long> login() {
        return null;
    }

}