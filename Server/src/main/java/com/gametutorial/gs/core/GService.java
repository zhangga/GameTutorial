/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务类注解
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 20:19
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface GService {

    /** 服务id前缀 */
    String prefix() default "";

    /** 所属线程名 */
    String threadName();

    /** 是否守护服务，起服就创建 */
    boolean daemon() default false;

    /** 守护服务数量 */
    int daemonNum() default 1;

}