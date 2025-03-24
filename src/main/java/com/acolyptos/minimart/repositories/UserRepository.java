package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.User;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

/*
 * Repository class for managing User-related database operations.
 * This class provides methods to insert, retrieve, and delete users
 * from MongoDB database.
*/
public class UserRepository {
  private final MongoCollection<User> userCollection;

  /*
   * Initializes the UserRepository and establishes a connection to
   * the "users" collection.
   */
  public UserRepository() {
    this.userCollection = MongoDB.getDatabase().getCollection("users", User.class);
  }

  /*
   * Inserts new user into the MongoDB collection
   *
   * @param user - The user object to be inserted
   * 
   * @return The unique ObjectId of the inserted User.
   *
   * @throws DatabaseException if the database error occurs during insertion.
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
   * Retrieves a User from the database by their unique ObjectId.
   * 
   * @param id - The unique ObjectID of the User.
   * 
   * @return The User object, if found.
   *
   * @throws ResourceNotFoundException if no user is found wit hthe given ID.
   *
   * @throws DatabaseException if an error occurs during the retrieval process.
   */
  public User getUserById(ObjectId id) {
    try {
      User user = userCollection
          .find(Filters.eq("_id", id))
          .first();

      if (user == null)
        throw new ResourceNotFoundException("User with id: " + id + " not found.");

      return user;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Execution Failes: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves a User from the database by their username
   * 
   * @param username - The username of the User.
   * 
   * @return The User object, if found
   *
   * @throws ResourceNotFoundException if no User is found with the given
   * username.
   *
   * @throws DatabaseException if an error occurs during retrieval process.
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
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Execution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves all Employee documents stored in the database.
   * 
   * @return A list of Employee objects.
   *
   * @throws ResourceNotFoundException if no employee is found.
   *
   * @throws DatabaseException if an error occurs during the retrieval process.
   */
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();

    // Checks if "users" collection is not empty"
    User checkIfEmpty = userCollection.find().first();
    if (checkIfEmpty == null)
      throw new ResourceNotFoundException("No users found in the database.");

    try {
      // Iterates through the collecation and adds all found users to the list,
      // User password is not included on the list.
      for (User user : userCollection
          .find()
          .projection(Projections.include("_id", "role", "username"))) {
        users.add(user);
      }

      return users;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Execution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeount: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Deletes a User from the database using their unique ObjectId.
   * 
   * @param id - The unique ObjectId of the User to be delted.
   * 
   * @return true if the deletion was successfull, false otherwise.
   *
   * @throws ResourceNotFoundException if no user is found with the given
   * ObjectId.
   *
   * @throws DatabaseException if error occurs during the deletion.
   */
  public boolean deleteUser(ObjectId id) {
    try {

      // Attempts to delete the user and checks if the deletion was successfull.
      DeleteResult result = userCollection.deleteOne(Filters.eq("_id", id));

      // Checks if user exists based on given unique ObjectId.
      if (result.getDeletedCount() == 0)
        throw new ResourceNotFoundException("User not found.");

      return true;
    } catch (MongoWriteConcernException exception) {
      throw new DatabaseException("Write Concern Failed: " + exception.getMessage(), exception);

    } catch (MongoWriteException exception) {
      throw new DatabaseException("Write Failed: " + exception.getError().getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }
}
