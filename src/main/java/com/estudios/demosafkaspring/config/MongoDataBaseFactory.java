package com.estudios.demosafkaspring.config;

import com.mongodb.client.MongoDatabase;
import org.springframework.dao.DataAccessException;

public interface MongoDataBaseFactory {

    MongoDatabase getDataBase() throws DataAccessException;
    MongoDatabase getDataBase(String dbName) throws DataAccessException;

}

