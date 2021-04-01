/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏服务的封装，挂载在游戏线程下，有如下特点：
 * 1.支持异步调用
 * 2.线程安全
 * 3.保证在回调到原游戏线程
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 15:11
 */
public abstract class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    /** 服务id */
    protected Object id;

    /** 所属线程 */
    protected GameThread thread = null;

    /**
     * 注意：子类如果重写了带参构造方法，必须也重写无参构造方法
     */
    public GameService() {

    }

    public GameService(GameThread thread, Object id) {
        String pre = GameServiceFactory.getGService(this.getClass()).prefix();
        if (StringUtils.isEmpty(pre)) {
            this.id = id;
        } else {
            this.id = pre + id;
        }
        this.thread = thread;
    }

    /**
     * 启动服务
     */
    public void startup() {
        thread.addService(this);
        GameServiceFactory.addDaemonServ(this);
    }

    /**
     * 获取服务的id
     * @return
     */
    public Object getId() {
        return id;
    }


    //-=-=-=-=-=-=-=-=-=-=-=-=创建代理对象时记录调用信息
    /** 调用信息 */
    private String callThreadId;
    private Object callSrvId;

    public String getCallThreadId() {
        return callThreadId;
    }

    public void setCallThreadId(String callThreadId) {
        this.callThreadId = callThreadId;
    }

    public Object getCallSrvId() {
        return callSrvId;
    }

    public void setCallSrvId(Object callSrvId) {
        this.callSrvId = callSrvId;
    }
}