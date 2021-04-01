/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 游戏服务
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 15:45
 */
public class GameNode {

    private static final Logger log = LoggerFactory.getLogger(GameNode.class);

    private final String nodeId;

    /** 线程 */
    private final ConcurrentMap<String, GameThread> threads = new ConcurrentHashMap<>();

    public GameNode(String nodeId) {
        this.nodeId = nodeId;
    }

    public void addThread(GameThread thread) {
        this.threads.put(thread.getId(), thread);
    }

    public GameThread getThread(String threadId) {
        if (threadId == null)
            return null;
        return threads.get(threadId);
    }

    public String getNodeId() {
        return nodeId;
    }
}