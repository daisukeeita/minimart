package com.acolyptos.minimart.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public interface DatabaseProvider {

  MongoDatabase getDatabase();

  MongoClient getMongoClient();

  void closeConnection();
}
