/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 线程包装类
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 15:17
 */
public class ThreadHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ThreadHandler.class);

    /** 游戏线程接口 */
    private final IThreadCase member;

    /** 是否在运行中 */
    private volatile boolean running = false;
    /** 是否在等待中 */
    private volatile boolean waiting = false;

    /** 心跳间隔(毫秒) */
    private int interval = 20;

    /** 执行计时 */
    private final Stopwatch watch;

    public ThreadHandler(IThreadCase member) {
        this.member = member;
        this.watch = Stopwatch.createUnstarted();
    }

    /**
     * 启动
     */
    public void startup() {
        // 已在运行中 忽略新的运行请求
        if (running) {
            return;
        }

        // 设置为运行状态
        running = true;

        // 启动线程
        start();
    }

    /**
     * 结束
     */
    public void cleanup() {
        // 非运行中 忽略结束请求
        if (!running) {
            return;
        }

        // 设置为停止状态
        running = false;
    }

    /**
     * 暂停当前线程
     */
    public void pauseT() {
        // 忽略同状态
        if (waiting) {
            return;
        }

        waiting = true;
    }

    /**
     * 恢复当前线程
     */
    public void resumeT() {
        // 忽略同状态
        if (!waiting) {
            return;
        }

        waiting = false;
    }

    @Override
    public void run() {
        // 开始前的准备
        member.caseStart();
        // 运行中就不断轮询
        while (running) {
            try {
                // 如果等待中，就不处理
                if (waiting) {
                    Thread.sleep(1);
                    continue;
                }

                // 执行业务
                watch.start();
                member.caseRunOnce();
                watch.stop();

                // 记录执行时间
                long timeRunning = watch.elapsed(TimeUnit.MILLISECONDS);
                log.debug("ThreadHandler:{} caseRunOnce costTime={}", this.getName(), timeRunning);

                // 尽量保证心跳帧率 每次执行后至少休息1毫秒 避免极端情况下连续执行占用过多系统资源
                if (timeRunning < interval) {
                    Thread.sleep(interval - timeRunning);
                } else {
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                log.error("ThreadHandler:{} caseRunOnce error={}", this.getName(), e);
                e.printStackTrace();
            } finally {
                watch.reset();
            }
        }
        // 结束时的清理
        member.caseStop();
    }

    /**
     * 毫秒
     * @param interval
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }
}