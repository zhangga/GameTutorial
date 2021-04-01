/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Flow;

/**
 * 异步调用方法
 * 可以在{@link GService}中的实例方法上加注解，返回类型为{@link Flow.Publisher<R>}，返回值为null即可
 *
 * @author jossy
 * @version 1.0
 * @date 2021/4/1 12:17
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GMethod {
}