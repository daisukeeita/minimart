package com.acolyptos.minimart.repositories;

import org.bson.types.ObjectId;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Product;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ProductRepository {

  private final MongoCollection<Product> productCollection;

  public ProductRepository (MongoCollection<Product> productCollection) {
    this.productCollection = productCollection;
  }

  public ProductRepository () {
    DatabaseProvider database = new MongoDB();
    productCollection = database
      .getDatabase()
      .getCollection("products", Product.class);
  }

  public void insertProduct (Product product) {
    try {
      productCollection.insertOne(product);

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

  public Product getProductById (ObjectId id) {
    try {
      Product product = productCollection.find(Filters.eq("_id", id)).first();

      if (product == null) {
        throw new ResourceNotFoundException(
          "Product with id: " + id + " not found."
        );
      }

      return product;
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
}
