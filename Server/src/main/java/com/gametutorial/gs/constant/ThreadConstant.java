/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.constant;

import com.gametutorial.gs.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * 游戏线程常量类
 * 必须将config.yaml中配置的线程池都定义在这
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 21:05
 */
public class ThreadConstant {

    private static final Logger log = LoggerFactory.getLogger(ThreadConstant.class);

    public static final String CONNECT = "connect";

    public static final String LOGIC = "logic";

    public static final String DB = "db";

    public static void checkValid() throws Exception {
        Field[] fields = ThreadConstant.class.getFields();
        assert fields.length == Server.getConfig().getThreadPool().size();
        for (Field field : ThreadConstant.class.getFields()) {
            try {
                String value = (String) field.get(null);
                if (!Server.getConfig().getThreadPool().containsKey(value)) {
                    throw new Exception("config.yaml dont contains key: " + value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                log.error("{}", e);
                throw e;
            }
        }
    }

}