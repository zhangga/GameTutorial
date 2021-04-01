/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.gametutorial.gs.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 游戏线程的封装
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 15:11
 */
public class GameThread implements IThreadCase {

    private static final Logger log = LoggerFactory.getLogger(GameThread.class);

    // 当前线程对应的GameThread
    private static ThreadLocal<GameThread> threadCurr = new ThreadLocal<>();

    /** 线程id */
    private final String id;

    /** java线程封装 */
    private final ThreadHandler thread;

    /** 所属node */
    private GameNode node;

    /** 当前线程开始时间(毫秒) */
    private long timeCurr = 0;

    /** 下属服务 */
    private final Map<Object, GameService> services = new HashMap<>();

    /** 接收到待处理的请求 */
    private final ConcurrentLinkedQueue<Call> calls = new ConcurrentLinkedQueue<>();
    /** 接收到的请求返回值 */
    private final ConcurrentLinkedQueue<Call> callResults = new ConcurrentLinkedQueue<>();
    /** 本次心跳需要处理的请求 */
    private final LinkedList<Call> pulseCalls = new LinkedList<>();
    /** 本次心跳需要处理的请求返回值 */
    private final LinkedList<Call> pulseCallResults = new LinkedList<>();

    public GameThread(String id) {
        this.id = id;
        this.thread = new ThreadHandler(this);
    }

    /**
     * 获取当前线程的游戏线程实例
     * @param <T>
     * @return
     */
    public static <T extends GameThread> T getCurrent() {
        Object thread = threadCurr.get();
        return (T) thread;
    }

    public void startup(GameNode node) {
        // 设置与Node的关系
        this.node = node;
        this.node.addThread(this);

        // 启动线程
        this.thread.setName(getId());
        this.thread.startup();

        log.info("GameThread startup, threadId={}", id);
    }

    @Override
    public void caseStart() {

    }

    @Override
    public void caseStop() {

    }

    @Override
    public void caseRunOnce() {
        pulse();
    }

    /**
     * 心跳操作
     */
    private void pulse() {
        // 记录下心跳开始时的时间戳 供之后的操作来统一时间
        timeCurr = System.currentTimeMillis();
        // 确认本心跳要执行的call及result
        pulseCallAffirm();
        // 执行Call请求
        pulseCalls();
        // 处理返回值
        pulseCallResults();
        // 驱动服务心跳
        pulseServices();
    }

    /**
     * 确认本心跳要执行的call及result
     */
    private void pulseCallAffirm() {
        // 本心跳要执行的call
        Call call = null;
        while ((call = calls.poll()) != null) {
            pulseCalls.addLast(call);
        }

        // 本心跳要执行的callResult
        Call callResult = null;
        while ((callResult = callResults.poll()) != null) {
            pulseCallResults.addLast(callResult);
        }
    }

    /**
     * 心跳中处理请求
     */
    private void pulseCalls() {
        while (!pulseCalls.isEmpty()) {
            Call call = pulseCalls.pop();
            dispatchCall(call);
        }
    }

    /**
     * 心跳中处理请求返回值
     */
    private void pulseCallResults() {
        while (!pulseCallResults.isEmpty()) {
            Call call = pulseCallResults.pop();
            FlowResult.Listener listener = call.callback.getListener();
            if (listener == null)
                continue;
            try {
                if (call.callback.getThrowable() != null) {
                    listener.onError(call.callback.getThrowable());
                }
                else {
                    listener.onResult(call.callback.getResult());
                }
            } catch (Exception e) {
                log.error("pulseCallResults call={}, error={}", call, e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行call请求
     * @param call
     */
    public void dispatchCall(Call call) {
        log.info("dispatchCall call={}", call);
        GameService service = services.get(call.toServiceId);
        if (service == null) {
            log.error("dispatchCall is null, call=", call);
            return;
        }

        // 执行方法
        Method method = GameServiceFactory.getGMethod(call.methodId);
        try {
            FlowResult flow = (FlowResult) method.invoke(service, call.args);
            Object ret = flow == null ? null : flow.getResult();
            call.callback.setResult(ret);
        } catch (Exception e) {
            log.error("dispatchCall call={}, error={}", call, e);
            e.printStackTrace();
            call.callback.setThrowable(e);
        }

        // 没有监听
        if (call.callback == null || call.callback.getListener() == null)
            return;
        // 回调到原线程
        GameThread thread = Server.getThread(call.fromThreadId);
        if (thread != null) {
            thread.addCallResult(call);
        }
        // 非游戏线程发起的调用
        else {
            GameServiceFactory.execute(call);
        }
    }

    private void pulseServices() {
        for (GameService service : services.values()) {
            try {
                service.pulse();
            } catch (Exception e) {
                log.error("pulseService={}, error={}", service.getId(), e);
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public void addService(GameService service) {
        services.put(service.getId(), service);
    }

    public void addCall(Call call) {
        calls.add(call);
    }

    public void addCallResult(Call call) {
        callResults.add(call);
    }

    public void setInterval(int interval) {
        this.thread.setInterval(interval);
    }
}