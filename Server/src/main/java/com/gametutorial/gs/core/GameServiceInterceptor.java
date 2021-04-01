/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.gametutorial.gs.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GameService动态代理
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 12:05
 */
public class GameServiceInterceptor implements MethodInterceptor {

    private static final AtomicLong idGen = new AtomicLong();

    @Transient
    private static final Logger log = LoggerFactory.getLogger(GameServiceInterceptor.class);

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        GMethod gm = method.getAnnotation(GMethod.class);
        if (gm == null) {
            Object result = null;
            if (!Modifier.isAbstract(method.getModifiers())) {
                result = methodProxy.invokeSuper(target, args);
            }
            return result;
        }

        // 异步调用
        GameService service = null;
        if (target instanceof GameService) {
            service = (GameService) target;
        }

        // 创建请求
        Call call = new Call();
        call.id = idGen.incrementAndGet();
        GameThread threadCurr = GameThread.getCurrent();
        call.fromThreadId = threadCurr == null ? null : threadCurr.getId();
        call.fromServiceId = threadCurr == null ? null : threadCurr.getId();
        call.toThreadId = service.getCallThreadId();
        call.toServiceId = service.getCallSrvId();
        call.methodId = GameServiceFactory.getGMethodId(method);
        call.args = args;
        call.callback = new FlowResult();
        // 都是本机调用
        GameThread thread = Server.getThread(call.toThreadId);
        thread.addCall(call);
        return call.callback;
    }

}