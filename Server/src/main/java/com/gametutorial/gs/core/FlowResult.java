/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import java.util.concurrent.SubmissionPublisher;

/**
 * GMethod返回结果
 * 实例方法中将真正要返回的数据包装下：return FlowResult.wrap(结果数据);
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 15:21
 */
public class FlowResult<T> {

    private T result = null;

    private Throwable throwable = null;

    private Listener<T> listener = null;

    public static <T> FlowResult<T> wrap(T result) {
        FlowResult<T> flow = new FlowResult<>();
        flow.result = result;
        return flow;
    }

    public void onResult(Listener<T> listener) {
        this.listener = listener;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Listener<T> getListener() {
        return listener;
    }

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    public static interface Listener<T> {
        public void onResult(T result);

        public void onError(Throwable throwable);
    }
}