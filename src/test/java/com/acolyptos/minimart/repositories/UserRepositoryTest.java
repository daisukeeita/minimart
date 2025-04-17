package com.acolyptos.minimart.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDBTest;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

class UserRepositoryTest {
  static DatabaseProvider mongoConnection;
  static MongoCollection<User> userCollection;
  static UserRepository userRepository;

  @BeforeEach
  void setup() {
    mongoConnection = new MongoDBTest();
    userCollection = mongoConnection
      .getDatabase().getCollection("users_test", User.class);
    userRepository = new UserRepository(userCollection);
    userCollection.drop();

    userCollection.createIndex(
      Indexes.ascending("username"), new IndexOptions().unique(true)
    );
  }

  @AfterEach
  void close() {
    mongoConnection.closeConnection();
  }


  @Test
  void insertAndFindUserTest() {
    User user = new User("userTest", "test123", Role.EMPLOYEE);
    
    ObjectId result = userRepository.insertUser(user); 
    assertNotNull(result, "Result should not be null upon inserting");

    User byUsername = userRepository.getUserByUsername("userTest");
    assertNotNull(
      byUsername, "User should not be null when fetched by username"
    );

    assertEquals("userTest", byUsername.getUsername());
    assertEquals("test123", byUsername.getPassword());
    assertEquals(Role.EMPLOYEE, byUsername.getRole());
  }

  @Test
  void duplicateUserTest () {
    User user = new User("userTest", "test123", Role.EMPLOYEE);
    userRepository.insertUser(user);

    DatabaseException exception = assertThrows(DatabaseException.class, () -> {
      userRepository.insertUser(user);
    });

    String message = exception.getMessage();
    assertTrue(message.contains("E11000"));
  }



  @Test
  void findNotFoundUserTest() {
    ResourceNotFoundException exception = 
      assertThrows(ResourceNotFoundException.class, 
      () -> userRepository.getUserByUsername("nonexistent")
    );

    String actualMessage = exception.getMessage();
    String expectedMessage = "User with username nonexistent not found.";

    assertTrue(
      actualMessage.contains(expectedMessage),
      "Expected message contains: " + expectedMessage 
      + " but was: " + actualMessage
    );
    
  }
}
