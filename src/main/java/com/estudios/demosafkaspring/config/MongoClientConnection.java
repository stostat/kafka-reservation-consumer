package com.estudios.demosafkaspring.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;


@Configuration
public class MongoClientConnection {

    @Bean
    public MongoClient mongoClient() {
        String connectionData = "mongodb://root:example@localhost:27017/testDb?authSource=admin&directConnection=true";
        return MongoClients.create(connectionData);
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
