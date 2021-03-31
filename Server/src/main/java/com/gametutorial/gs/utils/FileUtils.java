/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 文件工具类
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 13:55
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static <T> T loadYamlFile(String file, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            T ret = mapper.readValue(new File(file), clazz);
            return ret;
        } catch (Exception e) {
            log.error("loadYamlFile error={}", e);
            e.printStackTrace();
        }
        return null;
    }

}