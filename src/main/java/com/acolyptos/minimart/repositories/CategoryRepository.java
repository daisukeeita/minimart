package com.acolyptos.minimart.repositories;

import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.models.Category;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

public class CategoryRepository {
  private final MongoCollection<Category> categoryCollection;

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
}
