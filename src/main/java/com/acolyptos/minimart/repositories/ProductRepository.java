package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

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

  public ObjectId insertProduct (Product product) {
    try {
      InsertOneResult result =  productCollection.insertOne(product);
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

  public List<Product> getProductsByCategory (ObjectId categoryId) {
    List<Product> products = new ArrayList<>();

    try {
      for (Product product : 
      productCollection.find(Filters.eq("categoryId", categoryId))) {
        products.add(product);  
      }

      return products;

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

  public List<Product> getProductsBySupplier (ObjectId supplierId) {
    List<Product> products = new ArrayList<>();

    try {
      for (Product product : 
      productCollection.find(Filters.eq("supplierId", supplierId))) {
        products.add(product);  
      }

      return products;

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

  public boolean deleteProduct (ObjectId id) {
    try {
      DeleteResult result = productCollection.deleteOne(Filters.eq("_id", id));

      if (result.getDeletedCount() == 0) {
        throw new ResourceNotFoundException("Product not found.");
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
