/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs;

import com.alibaba.fastjson.JSONObject;
import com.gametutorial.gs.config.Args;
import com.gametutorial.gs.config.GameConf;
import com.gametutorial.gs.constant.ThreadConstant;
import com.gametutorial.gs.core.*;
import com.gametutorial.gs.mongo.MongoFactory;
import com.gametutorial.gs.net.TcpServer;
import com.gametutorial.gs.service.AccountService;
import com.gametutorial.gs.utils.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 游戏服启动入口
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/19 20:01
 */
@ComponentScan(value = "com.gametutorial.gs")
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private static GameConf config;

    private static GameNode node;

    public static void main(String[] args) throws Exception {
        // parse args
        Args arg = Args.parse(args);
        // load config
        Path confFile = Paths.get(arg.getConfPath(), "config.yaml");
        config = FileUtils.loadYamlFile(confFile.toString(), GameConf.class);
        log.info("load config = {}", JSONObject.toJSONString(config));

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(Server::shutdown));
        Server.beforeStartup();

        // start tcp server
        TcpServer tcpServer = new TcpServer(config);
        log.info("Game Server startup...");
        tcpServer.start();
    }

    private static void beforeStartup() throws Exception {
        ThreadConstant.checkValid();

        MsgHandlerFactory.init(Server.class);

        // init mongo
        MongoFactory.init(config);

        // init game node
        node = new GameNode("GameServer");
        // init game thread
        config.getThreadPool().forEach((name, num) -> {
            for (int i = 0; i < num; i++) {
                GameThread thread = new GameThread(name + i);
                thread.startup(node);
            }
        });

        // init game service
        AnnotationConfigApplicationContext context = null;
        try {
            context = new AnnotationConfigApplicationContext(Server.class);
        } catch (UnsatisfiedDependencyException e) {
            log.error("init game service failed, undefined default constructor, class={}, error={}", e.getResourceDescription(), e);
            throw e;
        }
        Map<String, GameService> map = context.getBeansOfType(GameService.class);
        for (GameService gs : map.values()) {
            Class<? extends GameService> clazz = gs.getClass();
            GService service = clazz.getAnnotation(GService.class);
            if (service == null) {
                log.error("GameService class={}, cannot found @GService, it dont work well");
                continue;
            }

            GameServiceFactory.addClass(clazz);
            // 守护服务启动
            if (service.daemon()) {
                // 根据该线程数量随机起始值，然后顺序添加
                int start = RandomUtils.nextInt(0, config.getThreadPool().get(service.threadName()));
                for (int i = 0; i < service.daemonNum(); i++) {
                    // 对应的线程
                    GameThread thread = getThread(service.threadName(), i+start);
                    GameService serv = GameServiceFactory.newInstance(clazz, thread, i);
                    serv.startup();
                    log.info("GameService startup, {}", serv);
                }
            }
        }
    }

    private static void shutdown() {
        log.info("============================before server shutdown============================");
    }

    public static GameNode getNode() {
        return node;
    }

    public static GameThread getThread(String threadName, int id) {
        int num = config.getThreadPool().getOrDefault(threadName, 0);
        if (num < 1) {
            log.error("cannot found threadName={}", threadName);
            return null;
        }
        id %= num;
        return node.getThread(threadName + id);
    }

    public static GameThread getThread(String threadId) {
        return node.getThread(threadId);
    }

    public static GameConf getConfig() {
        return config;
    }

    // 测试代码
    public static void test() {
        AccountService as = GameServiceFactory.createProxy(AccountService.class);
        FlowResult<String> flow = as.getString("this is string");
        flow.onResult(new FlowResult.Listener<String>() {
            @Override
            public void onResult(String result) {
                System.out.println(result);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}