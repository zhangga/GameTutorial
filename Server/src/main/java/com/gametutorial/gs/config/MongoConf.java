/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MongoDB配置
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 14:01
 */
public class MongoConf {

    @JsonProperty("mongoConn")
    private String mongoConn = "mongodb://localhost";

    @JsonProperty("mongoDataBase")
    private String mongoDataBase = "database";

    public String getMongoConn() {
        return mongoConn;
    }

    public void setMongoConn(String mongoConn) {
        this.mongoConn = mongoConn;
    }

    public String getMongoDataBase() {
        return mongoDataBase;
    }

    public void setMongoDataBase(String mongoDataBase) {
        this.mongoDataBase = mongoDataBase;
    }

}