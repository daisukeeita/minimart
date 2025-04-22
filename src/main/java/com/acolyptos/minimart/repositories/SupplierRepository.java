package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Supplier;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class SupplierRepository {

  private final MongoCollection<Supplier> supplierCollection;

  public SupplierRepository () {
    DatabaseProvider database = new MongoDB();
    this.supplierCollection = database
      .getDatabase()
      .getCollection("suppliers", Supplier.class);
  }

  public SupplierRepository (MongoCollection<Supplier> supplierCollection) {
    this.supplierCollection = supplierCollection;
  }

  public ObjectId insertSupplier (Supplier supplier) {
    try {
      InsertOneResult result = supplierCollection.insertOne(supplier);
      return result.getInsertedId().asObjectId().getValue();
    } catch (MongoWriteException exception) {
      // Handles issues like duplicate key errors or other data constraints
      System.err.println("Write Error " + exception.getError().getMessage());
      throw new DatabaseException(
        "Write Error: " + exception.getError().getMessage(), exception
      );

    } catch (MongoWriteConcernException exception) {
      // Handles issues related to Write Concern
      System.err.println("Write Concern Error: " + exception.getMessage());
      throw new DatabaseException(
        "Write concern error: " + exception.getMessage(), exception
      );

    } catch (MongoException exception) {
      // Handles other MongoDB exception
      System.err.println("Database Error: " + exception.getMessage());
      throw new DatabaseException(
        "MongoDB error: " + exception.getMessage(), exception
      );

    } catch (Exception exception) {
      System.err.println("Unexpected Error: " + exception.getMessage());
      throw new DatabaseException(
        "Unexpected error: " + exception.getMessage(), exception
      );
    }
  }

  public Supplier getSupplierById (ObjectId id) {
    try {
      Supplier supplier = supplierCollection
        .find(Filters.eq("_id", id))
        .first();

      if (supplier == null) {
        throw new ResourceNotFoundException(
          "Supplier with id: " + id + " not found."
        );
      }

      return supplier;
    } catch (MongoQueryException exception) {
      throw new DatabaseException(
        "Query Execution Failes: " + exception.getMessage(), exception
      );

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException(
        "Database Timeout: " + exception.getMessage(), exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), exception
      );
    }
  }

  public Supplier getSupplierByName (String name) {
    try {
      Supplier supplier = supplierCollection
        .find(Filters.eq("name", name))
        .first();

      if (supplier == null) {
        throw new ResourceNotFoundException(
          "Supplier with name: " + name + " not found."
        );
      }
        
      return supplier;
      
    } catch (MongoQueryException exception) {
      throw new DatabaseException(
        "Query Execution Failes: " + exception.getMessage(), exception
      );

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException(
        "Database Timeout: " + exception.getMessage(), exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), exception
      );
    }
  }

  public List<Supplier> getAllSuppliers () {
    List<Supplier> suppliers = new ArrayList<>();

    Supplier checkIfEmpty = supplierCollection.find().first();
    if (checkIfEmpty == null) {
      throw new ResourceNotFoundException(
        "No suppliers found in the database."
      );
    }

    try {
      for (Supplier supplier : supplierCollection.find()) {
        suppliers.add(supplier);
      }

      return suppliers;
    } catch (MongoQueryException exception) {
      throw new DatabaseException(
        "Query Execution Failed: " + exception.getMessage(), exception
      );

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException(
        "Database Timeount: " + exception.getMessage(), exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), exception
      );
    }
  }

  public boolean deleteSupplier (ObjectId id) {
    try {
      DeleteResult result = supplierCollection.deleteOne(Filters.eq("_id", id));

      if (result.getDeletedCount() == 0) {
        throw new ResourceNotFoundException(
          "Supplier not found."
        );
      }

      return true;
    } catch (MongoWriteConcernException exception) {
      throw new DatabaseException(
        "Write Concern Failed: " + exception.getMessage(), exception
      );

    } catch (MongoWriteException exception) {
      throw new DatabaseException(
        "Write Failed: " + exception.getError().getMessage(), exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), exception
      );
    }
  }

  public void deleteAll () throws Exception {
    try {
      DeleteResult result = supplierCollection.deleteMany(null);

      if (result.getDeletedCount() == 0) {
        throw new Exception("Deletion Failed.");
      }
    } catch (MongoWriteConcernException exception) {
      throw new DatabaseException(
        "Write Concern Failed: " + exception.getMessage(), exception
      );

    } catch (MongoWriteException exception) {
      throw new DatabaseException(
        "Write Failed: " + exception.getError().getMessage(), exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), exception
      );
    }
  }
}
