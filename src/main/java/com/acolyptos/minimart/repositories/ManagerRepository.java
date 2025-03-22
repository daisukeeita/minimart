package com.acolyptos.minimart.repositories;

import java.util.List;
import java.util.ArrayList;
import org.bson.types.ObjectId;

import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.models.Manager;

import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class ManagerRepository {
  private final MongoCollection<Manager> managerCollection;

  public ManagerRepository() {
    this.managerCollection = MongoDB.getDatabase().getCollection("managers", Manager.class);
  }

  /*
   * Inserts/Saves a new Manager into MongoDB manager collection
   * 
   * @param manager - represents the manager object instance
   * 
   * @return generated ObjectId after insertion process of the manager object.
   */
  public ObjectId insertManager(Manager manager) {
    try {
      InsertOneResult result = managerCollection.insertOne(manager);
      return result.getInsertedId().asObjectId().getValue();
    } catch (Exception e) {
      throw new DatabaseException("Failed to insert Manager - " + manager.getName(), e);
    }
  }

  /*
   * Finds and retrieves a Manager using given unique identifier
   *
   * @param id - represents the ObjectId that will using to find a Manager
   *
   * @return the Manager object if found, else null
   */
  public Manager getManagerById(ObjectId id) {
    try {
      Manager manager = managerCollection.find(Filters.eq("_id", id)).first();
      if (manager == null) {
        throw new ResourceNotFoundException("Manager with id " + id + " not found");
      }

      return manager;
    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrieving manager - " + e.getMessage(), e);
    }
  }

  /*
   * Finds and retrieves a Manager using given unique identifier
   *
   * @param name - represents the name that will be using to find a Manager
   *
   * @return the Manager object if found, else null
   */
  public Manager getMangerbyName(String name) {
    try {
      Manager manager = managerCollection.find(Filters.eq("name", name)).first();
      if (manager == null) {
        throw new ResourceNotFoundException("Manager with name " + name + " not found");
      }

      return manager;
    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrievening Manager - " + e.getMessage(), e);
    }
  }

  /*
   * Retrieves all of the Managers from the database collection
   *
   * @return Lists of Manager object if found, else null
   */
  public List<Manager> getAllManagers() {
    List<Manager> managers = new ArrayList<>();

    try {
      for (Manager manager : managerCollection.find()) {
        managers.add(manager);
      }
    } catch (Exception e) {
      throw new DatabaseException("List was empty.", e);
    }

    return managers;
  }

  /*
   * Deletes a manager from database collection, if found.
   *
   * @param id - represents and ObjectId of the manager.
   *
   * @return boolean values based on the result
   */
  public boolean deleteEmployee(ObjectId id) {
    try {
      DeleteResult result = managerCollection.deleteOne(Filters.eq("_id", id));

      return result.getDeletedCount() > 0;
    } catch (Exception e) {
      throw new DatabaseException("Failed to delete Manager.", e);
    }
  }
}
