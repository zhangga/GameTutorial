/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.config;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Args
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 13:43
 */
public class Args {

    private static final Logger log = LoggerFactory.getLogger(Args.class);

    @Option(name="-c", usage="config path")
    private String confPath="./conf";

    public static Args parse(String[] args) {
        Args arg = new Args();
        CmdLineParser parser = new CmdLineParser(arg);
        try {
            parser.parseArgument(args);
            log.info("config path = {}", arg.getConfPath());
            return arg;
        } catch(CmdLineException e) {
            e.printStackTrace();
            log.error("java SampleMain [options...] arguments..., error = {}", e);
        }
        return null;
    }

    public String getConfPath() {
        return confPath;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }
}