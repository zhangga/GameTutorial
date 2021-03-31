/*
 * Copyright (c) [2021] [bytedance.inc] All rights reserved.
 */

package com.gametutorial.gs.mongo;

import com.gametutorial.gs.config.GameConf;
import com.gametutorial.gs.db.PlayerDB;
import com.mongodb.reactivestreams.client.*;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * TODO
 *
 * @author jossy
 * @version 1.0
 * @date 2021/3/30 16:00
 */
public class MongoFactory {

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
        PlayerDB playerDB = new PlayerDB();
        playerDB.setLv(1);
        playerDB.setExp(100);
        playerDB.setName("测试");
        playerDB.setOpenId("zzq");
        Mono<PlayerDB> mono = template.save(playerDB);
        mono.subscribe((data) -> {
            System.out.println(data);
        });
    }

    public static ReactiveMongoTemplate getTemplate() {
        return template;
    }
}