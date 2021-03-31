/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 服务器配置
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 13:41
 */
public class ServerConf {

    @JsonProperty("port")
    private int port = 10000;

    @JsonProperty("bossThread")
    private int bossThread = 1;

    @JsonProperty("workerThread")
    private int workerThread = 4;

    @JsonProperty("idleTimeoutSec")
    private int idleTimeoutSec = 60;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThread() {
        return bossThread;
    }

    public void setBossThread(int bossThread) {
        this.bossThread = bossThread;
    }

    public int getWorkerThread() {
        return workerThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public int getIdleTimeoutSec() {
        return idleTimeoutSec;
    }

    public void setIdleTimeoutSec(int idleTimeoutSec) {
        this.idleTimeoutSec = idleTimeoutSec;
    }
}