/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.gametutorial.gs.service.PlayerService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GameServiceFactory
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 11:53
 */
public class GameServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(GameServiceFactory.class);

    private static final GameServiceInterceptor interceptor = new GameServiceInterceptor();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=预处理数据-=-=-=-=-=-=-=-=-=-=-=-=-=-=//
    private static final Map<Class<? extends GameService>, Constructor> constructors = new HashMap<>();
    private static final Map<Class<? extends GameService>, GService> gServices = new HashMap<>();
    private static final Map<Class<? extends GameService>, Enhancer> enhancerCache = new HashMap<>();
    // method <-> id
    private static final BiMap<Method, Integer> gMethods = HashBiMap.create();

    // 服务分布的线程id
    private static final ArrayListMultimap<Class<? extends GameService>, String> daemonThreadIds = ArrayListMultimap.create();
    private static final ArrayListMultimap<String, Object> daemonServIds = ArrayListMultimap.create();

    private static final AtomicInteger idGen = new AtomicInteger();

    // 游戏线程之外的回调
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void addClass(Class<? extends GameService> clazz) throws Exception {
        try {
            Constructor constructor = clazz.getConstructor();
            constructors.put(clazz, constructor);
            gServices.put(clazz, clazz.getAnnotation(GService.class));
            for (Method method : clazz.getMethods()) {
                GMethod gm = method.getAnnotation(GMethod.class);
                if (gm == null)
                    continue;
                if (method.getReturnType() != FlowResult.class) {
                    String msg = "class=" + clazz.getName() + ", method=" + method.getName() + " return type must be FlowResult.";
                    log.error(msg);
                    throw new Exception(msg);
                }
                gMethods.put(method, idGen.incrementAndGet());
            }
            // 动态代理
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(interceptor);
            enhancerCache.put(clazz, enhancer);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static <T extends GameService> T newInstance(Class<T> clazz, GameThread thread, Object id) {
        Constructor constructor = constructors.get(clazz);
        if (constructor == null) {
            log.error("cannot found class={} Constructor", clazz);
            return null;
        }
        try {
            T inst = (T) constructor.newInstance();
            inst.thread = thread;
            inst.id = id;
            return inst;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("create instance class={}, error={}", clazz, e);
        }
        return null;
    }

    /**
     * 创建一个随机的daemon代理
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends GameService> T createProxy(Class<T> clazz) {
        GService gs = gServices.get(clazz);
        if (gs == null)
            return null;
        // thread id
        List<String> threadIds = daemonThreadIds.get(clazz);
        String threadId = threadIds.get(RandomUtils.nextInt(0, threadIds.size()));
        // service id
        List<Object> servIds = daemonServIds.get(threadId);
        Object servId = servIds.get(RandomUtils.nextInt(0, servIds.size()));
        // create proxy
        Enhancer enhancer = enhancerCache.get(clazz);
        T bean = (T) enhancer.create();
        bean.setCallThreadId(threadId);
        bean.setCallSrvId(servId);
        return bean;
    }

    public static <T extends GameService> T createProxy(Class<T> clazz, Object id) {
        Enhancer enhancer = enhancerCache.get(clazz);
        T bean = (T) enhancer.create();
        bean.id = id;
        return bean;
    }

    protected static void execute(Call call) {
        if (call.callback == null || call.callback.getListener() == null)
            return;
        executor.submit(() -> {
            var callback = call.callback;
            if (callback.getThrowable() != null) {
                callback.getListener().onError(callback.getThrowable());
            }
            else {
                callback.getListener().onResult(callback.getResult());
            }
        });
    }

    public static GService getGService(Class<? extends GameService> clazz) {
        return gServices.get(clazz);
    }

    public static void addDaemonServ(GameService service) {
        GService gs = gServices.get(service.getClass());
        if (!gs.daemon()) {
            log.debug("GameService:{} is not daemon, cannot add to Serv2Thread", service.getClass());
            return;
        }
        daemonThreadIds.put(service.getClass(), service.thread.getId());
        daemonServIds.put(service.thread.getId(), service.getId());
    }

    public static int getGMethodId(Method method) {
        return gMethods.getOrDefault(method, 0);
    }

    public static Method getGMethod(int id) {
        return gMethods.inverse().get(id);
    }

}