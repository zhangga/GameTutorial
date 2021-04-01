/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.service;

import com.gametutorial.gs.constant.ThreadConstant;
import com.gametutorial.gs.core.FlowResult;
import com.gametutorial.gs.core.GMethod;
import com.gametutorial.gs.core.GameService;
import com.gametutorial.gs.core.GService;

/**
 * 账号服务
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 20:18
 */
@GService(threadName = ThreadConstant.LOGIC, daemon = true, daemonNum = 2)
public class AccountService extends GameService {

    @GMethod
    public FlowResult<String> getString(String value) {
        return FlowResult.wrap("result = " + value);
    }

}