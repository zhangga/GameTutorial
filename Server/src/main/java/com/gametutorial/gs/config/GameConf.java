/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 游戏服务器配置
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 14:02
 */
public class GameConf {

    @JsonProperty("serverConf")
    private ServerConf serverConf;

    @JsonProperty("mongoConf")
    private MongoConf mongoConf;

    @JsonProperty("threadPool")
    private Map<String, Integer> threadPool;

    public ServerConf getServerConf() {
        return serverConf;
    }

    public void setServerConf(ServerConf serverConf) {
        this.serverConf = serverConf;
    }

    public MongoConf getMongoConf() {
        return mongoConf;
    }

    public void setMongoConf(MongoConf mongoConf) {
        this.mongoConf = mongoConf;
    }

    public Map<String, Integer> getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(Map<String, Integer> threadPool) {
        this.threadPool = threadPool;
    }
}