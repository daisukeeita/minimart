package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Category;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

public class CategoryRepository {
  private final MongoCollection<Category> categoryCollection;
  private final Logger LOG = LoggerFactory.getLogger(CategoryRepository.class);

  public CategoryRepository (MongoCollection<Category> categoryCollection) {
    this.categoryCollection = categoryCollection;
  }

  public CategoryRepository () {
    DatabaseProvider database = new MongoDB();
    this.categoryCollection = database
      .getDatabase()
      .getCollection("categories", Category.class);
  }

  public void insertCategory (Category category) {
    try {
      categoryCollection.insertOne(category);

    } catch (MongoWriteException exception) {
      // Handles issues like duplicate key errors or other data constraints
      LOG.error("Write Error " + exception.getError().getMessage());
      throw new DatabaseException(
        "Write Error: " + exception.getError().getMessage(), 
        exception
      );

    } catch (MongoWriteConcernException exception) {
      // Handles issues related to Write Concern
      LOG.error("Write Concern Error: " + exception.getMessage());
      throw new DatabaseException(
        "Write concern error: " + exception.getMessage(), 
        exception
      );

    } catch (MongoException exception) {
      // Handles other MongoDB exception
      LOG.error("Database Error: " + exception.getMessage());
      throw new DatabaseException(
        "MongoDB error: " + exception.getMessage(), 
        exception
      );

    } catch (Exception exception) {
      LOG.error("Unexpected Error: " + exception.getMessage());
      throw new DatabaseException(
        "Unexpected error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public Category getCategoryById (ObjectId id) {
    try {
      Category category = categoryCollection
        .find(Filters.eq("_id", id))
        .first();

      if (category == null) {
        throw new ResourceNotFoundException("Category with id: " + id + " not found.");
      }

      return category;

    } catch (MongoQueryException exception) {
      throw new DatabaseException(
        "Query Excecution Failed: " + exception.getMessage(), 
        exception
      );

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException(
        "Database Timeout: " + exception.getMessage(), 
        exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public List<Category> getAllCategories() {
    List<Category> categories = new ArrayList<>();

    Category checkIfEmpty = categoryCollection.find().first();
    if (checkIfEmpty == null) {
      throw new ResourceNotFoundException("No category found in the database.");
    }

    try {
      for (Category category : categoryCollection.find()) {
        categories.add(category);
      }

      return categories;

    } catch (MongoQueryException exception) {
      throw new DatabaseException(
        "Query Execution Failed: " + exception.getMessage(), 
        exception
      );

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException(
        "Database Timeout: " + exception.getMessage(), 
        exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public boolean deleteCategory (ObjectId id) {
    try {
      DeleteResult result = categoryCollection.deleteOne(Filters.eq("_id", id));

      if (result.getDeletedCount() == 0) {
        throw new ResourceNotFoundException("Category not found.");
      }

      return true;

    } catch (MongoWriteConcernException exception) {
      throw new DatabaseException(
        "Write Concern Failed: " + exception.getMessage(), 
        exception
      );

    } catch (MongoWriteException exception) {
      throw new DatabaseException(
        "Write Failed: " + exception.getError().getMessage(), 
        exception
      );

    } catch (MongoException exception) {
      throw new DatabaseException(
        "MongoDB Error: " + exception.getMessage(), 
        exception
      );
    }
  }
}
