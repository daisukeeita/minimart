package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.User;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class UserRepository {
  private final MongoCollection<User> userCollection;

  public UserRepository() {
    this.userCollection = MongoDB.getDatabase().getCollection("users", User.class);
  }

  /*
   * Inserts new user into the MongoDB collection
   *
   * @param user - The user object to be inserted
   * 
   * @return inserted unique identifier of the User
   */
  public ObjectId insertUser(User user) {
    try {
      InsertOneResult result = userCollection.insertOne(user);
      return result.getInsertedId().asObjectId().getValue();

    } catch (MongoWriteException exception) {
      // Handles issues like duplicate key errors or other data constraints
      System.err.println("Write Error " + exception.getError().getMessage());
      throw new DatabaseException("Write Error: " + exception.getError().getMessage(), exception);

    } catch (MongoWriteConcernException exception) {
      // Handles issues related to Write Concern
      System.err.println("Write Concern Error: " + exception.getMessage());
      throw new DatabaseException("Write concern error: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      // Handles other MongoDB exception
      System.err.println("Database Error: " + exception.getMessage());
      throw new DatabaseException("MongoDB error: " + exception.getMessage(), exception);

    } catch (Exception exception) {
      System.err.println("Unexpected Error: " + exception.getMessage());
      throw new DatabaseException("Unexpected error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves a User by their unique ID
   * 
   * @param id Expected unique identifier of the user
   * 
   * @return class name and memory address (ex. User@1908201a)
   * override toString() method as suggested
   */
  public User getUserById(ObjectId id) {
    try {
      User user = userCollection
          .find(Filters.eq("_id", id))
          .first();
      if (user == null) {
        throw new ResourceNotFoundException("User with id: " + id + " not found.");
      }
      return user;
    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrieving User - " + e.getMessage(), e);
    }
  }

  /*
   * Retrieves a user by their unique ID
   * 
   * @param name Expected name to find the User
   * 
   * @return class name and memory address (ex. User@1908201a)
   * override toString() method as suggested
   */
  public User getUserByUsername(String username) {
    try {
      User user = userCollection
          .find(Filters.eq("username", username))
          .first();
      if (user == null) {
        throw new ResourceNotFoundException("User with username " + username + " not found.");
      }
      return user;
    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrieving User - " + e.getMessage(), e);
    }
  }

  /*
   * Retrieves all the Users
   * 
   * @return lists of all users if found, or null if not found
   */
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();

    for (User user : userCollection
        .find()
        .projection(Projections.include("_id", "role", "username"))) {
      users.add(user);
    }

    return users;
  }

  /*
   * Deletes a user from the collection by their ID
   * 
   * @param id The unique identifier of the User to be deleted
   * 
   * @return true if the deletion was successfull, false otherwise.
   */
  public boolean deleteUser(ObjectId id) {
    try {
      DeleteResult result = userCollection.deleteOne(Filters.eq("_id", id));

      return result.getDeletedCount() > 0;
    } catch (Exception e) {
      throw new DatabaseException("Failed to delete User.", e);
    }
  }
}
