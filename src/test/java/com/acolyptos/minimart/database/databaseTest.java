package com.acolyptos.minimart.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import com.acolyptos.minimart.models.User;
import com.mongodb.client.MongoDatabase;

public class databaseTest {

  @AfterAll
  static void close() {
    DatabaseProvider mongoConnection = new MongoDBTest();
    mongoConnection.closeConnection();
  }

  @Test
  void databaseConnectionTest() {
    DatabaseProvider mongoConnection = new MongoDBTest();
    MongoDatabase mongoDatabase = mongoConnection.getDatabase();
    assertNotNull(mongoDatabase);
    assertEquals(
      "users_test", 
      mongoDatabase.getCollection("users_test", User.class)
        .getNamespace().getCollectionName()
    );
  }
}
