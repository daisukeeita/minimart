package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Manager;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

/*
 * Repository class for managing Manager-related database operations.
 * This class provides methods to insert, retrieve, and delte employees
 * from MongoDB database.
*/
public class ManagerRepository {
  private final MongoCollection<Manager> managerCollection;

  /*
   * Initializes the ManagerRepository and estableshes a connection to
   * the "managers" colloection.
   */
  public ManagerRepository() {
    DatabaseProvider mongoDB = new MongoDB();
    this.managerCollection = mongoDB.getDatabase().getCollection("managers", Manager.class);
  }

  /*
   * Inserts new manager into the MongoDB collection.
   * 
   * @param manager - The manager object to be inserted.
   * 
   * @return The unique ObjectId of the inserted Manager.
   *
   * @throws DatabaseException if the database error occurs during insertion.
   */
  public ObjectId insertManager(Manager manager) {
    try {
      InsertOneResult result = managerCollection.insertOne(manager);
      return result.getInsertedId().asObjectId().getValue();
    } catch (MongoWriteException exception) {
      // Handles issues like duplicate key errors or other data constraints
      System.err.println("Write Error: " + exception.getError().getMessage());
      throw new DatabaseException("Write Error: " + exception.getError().getMessage(), exception);

    } catch (MongoWriteConcernException exception) {
      // Handles issues related to write concerns (e.g., Failed Write Acknowledgement)
      System.err.println("Write Concern Error: " + exception.getMessage());
      throw new DatabaseException("Write concern error: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      // Catches other general MongoDB-related exceptions
      System.err.println("Database Error: " + exception.getMessage());
      throw new DatabaseException("MongoDB error: " + exception.getMessage(), exception);

    } catch (Exception exception) {
      // Catches any other Unexpected Runtime Exceptions
      System.err.println("Unexpected Error: " + exception.getMessage());
      throw new DatabaseException("Unexpected error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves a Manager from the database by their unique ObjectId.
   *
   * @param id - The unique ObjectId of the Manager.
   *
   * @return the Manager object, if found.
   *
   * @throws ResourceNotFoundException if no manager is found with the given ID.
   *
   * @throw DatabaseException if an error occurs during the retrieval process.
   */
  public Manager getManagerById(ObjectId id) {
    try {
      Manager manager = managerCollection
          .find(Filters.eq("_id", id))
          .first();

      if (manager == null) {
        throw new ResourceNotFoundException("Manager with id " + id + " not found");
      }

      return manager;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves a Manager from the database by their name.
   *
   * @param name - The name of the Manager.
   *
   * @return the Manager object, if found.
   *
   * @throws ResourceNotFoundException if no manager is found with the given name.
   *
   * @throws DatabaseException if an error occurs during retrieval process.
   */
  public Manager getMangerbyName(String name) {
    try {
      Manager manager = managerCollection.find(Filters.eq("name", name)).first();

      if (manager == null) {
        throw new ResourceNotFoundException("Manager with name " + name + " not found");
      }

      return manager;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrives all Manager documents stored in the database.
   *
   * @return A list of Manager objects.
   *
   * @throws ResourceNotFoundException if not manager is found.
   *
   * @throws DatabaseException if an error occurs during the retrieval process.
   */
  public List<Manager> getAllManagers() {
    List<Manager> managers = new ArrayList<>();

    // Checks if "managers" collection is not empty.
    Manager checkIfEmpty = managerCollection.find().first();
    if (checkIfEmpty == null)
      throw new ResourceNotFoundException("No managers found in the database.");

    try {
      // Iterates through the collection and adds all found managers to the list
      for (Manager manager : managerCollection.find()) {
        managers.add(manager);
      }

      return managers;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }

  }

  /*
   * Deletes a Manager from the database using their unique ObjectId.
   *
   * @param id - The unique ObjectId of the Manager to be deleted.
   *
   * @return true if the deletion was successful.
   *
   * @throws ResourceNotFoundException if no manager was found with the given
   * ObjectId.
   *
   * @throws DatabaseException if error occurs during the deletion.
   */
  public boolean deleteEmployee(ObjectId id) {
    try {
      // Attempts to delete the manager and checks if the deletion was successful
      DeleteResult result = managerCollection.deleteOne(Filters.eq("_id", id));

      // Checks if manager exists based on given unique ObjectId.
      if (result.getDeletedCount() == 0)
        throw new ResourceNotFoundException("Manager not found.");

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
