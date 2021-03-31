/*
 * Copyright (c) [2021] [bytedance.inc] All rights reserved.
 */

package com.gametutorial.gs;

import com.alibaba.fastjson.JSONObject;
import com.gametutorial.gs.config.Args;
import com.gametutorial.gs.config.GameConf;
import com.gametutorial.gs.mongo.MongoFactory;
import com.gametutorial.gs.net.TcpServer;
import com.gametutorial.gs.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 游戏服启动入口
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/19 20:01
 */
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        // parse args
        Args arg = Args.parse(args);
        // load config
        Path confFile = Paths.get(arg.getConfPath(), "config.yaml");
        GameConf config = FileUtils.loadYamlFile(confFile.toString(), GameConf.class);
        log.info("load config = {}", JSONObject.toJSONString(config));
        // init mongo
        MongoFactory.init(config);
        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(Server::shutdown));
        // start tcp server
        TcpServer tcpServer = new TcpServer(config);
        log.info("Game Server startup...");
        tcpServer.start();
    }

    private static void shutdown() {
        log.info("============================before server shutdown============================");
    }

}