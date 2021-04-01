/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import java.util.Arrays;

/**
 * 封装一个请求
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 16:23
 */
public class Call {

    /** 请求ID */
    protected long id;

    /** 发送方NodeId */
//    protected String fromNodeId;
    /** 发送方ThreadId */
    protected String fromThreadId;
    /** 发送方ServiceId */
    protected Object fromServiceId;

    /** 接收方NodeId */
//    protected String toNodeId;
    /** 接收方ThreadId */
    protected String toThreadId;
    /** 接收方ServiceId */
    protected Object toServiceId;

    /** 调用method */
    protected int methodId;
    protected Object[] args;

    /** 请求监听 */
    protected FlowResult callback;

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", fromThreadId='" + fromThreadId + '\'' +
                ", fromServiceId=" + fromServiceId +
                ", toThreadId='" + toThreadId + '\'' +
                ", toServiceId=" + toServiceId +
                ", methodId=" + methodId +
                ", args=" + Arrays.toString(args) +
                ", callback=" + callback +
                '}';
    }
}