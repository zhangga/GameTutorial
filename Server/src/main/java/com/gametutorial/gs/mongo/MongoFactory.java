/*
 * Copyright (c) [2021] [gametutorial.inc] All rights reserved.
 */

package com.gametutorial.gs.mongo;

import com.gametutorial.gs.config.GameConf;
import com.gametutorial.gs.db.PlayerDB;
import com.mongodb.reactivestreams.client.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Mongo工厂方法
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 16:00
 */
public class MongoFactory {

    private static final Logger log = LoggerFactory.getLogger(MongoFactory.class);

    private static ReactiveMongoTemplate template;

    public static void init(GameConf gameConf) {
        // mongo client
        MongoClient client = MongoClients.create(gameConf.getMongoConf().getMongoConn());
        ReactiveMongoDatabaseFactory factory = new SimpleReactiveMongoDatabaseFactory(client, gameConf.getMongoConf().getMongoDataBase());

        MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());

        MongoMappingContext context = new MongoMappingContext();
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        context.setAutoIndexCreation(true);
        context.afterPropertiesSet();

        MappingMongoConverter converter = new MappingMongoConverter(ReactiveMongoTemplate.NO_OP_REF_RESOLVER, context);
        converter.setCustomConversions(conversions);
        converter.setCodecRegistryProvider(factory);
        converter.afterPropertiesSet();

        template = new ReactiveMongoTemplate(factory, converter);

        // save
//        PlayerDB playerDB = new PlayerDB();
//        playerDB.setLv(1);
//        playerDB.setExp(100);
//        playerDB.setName("测试");
//        playerDB.setOpenId("zzq");
//        Mono<PlayerDB> mono = template.save(playerDB);
//        mono.subscribe((data) -> {
//            log.info("data={}", data);
//        });
        // find
        Mono<PlayerDB> find = template.findById(new ObjectId("6063d9b9e1280b7b9c6c2705"), PlayerDB.class);
        find.subscribe((data) -> {
            log.info("data={}", data);
        });
    }

    public static ReactiveMongoTemplate getTemplate() {
        return template;
    }
}