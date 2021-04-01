/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import com.google.protobuf.GeneratedMessageV3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息处理方法上加注解
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 21:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MsgReceiver {

    Class<? extends GeneratedMessageV3> value();

}