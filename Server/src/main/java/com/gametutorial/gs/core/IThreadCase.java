/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.core;

/**
 * 线程接口
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/31 15:15
 */
public interface IThreadCase {

    /**
     * 线程启动时执行的操作
     */
    public void caseStart();

    /**
     * 线程结束时执行的操作
     */
    public void caseStop();

    /**
     * 线程每帧执行的操作
     */
    public void caseRunOnce();

}