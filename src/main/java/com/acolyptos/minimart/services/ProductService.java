package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Product;
import com.acolyptos.minimart.repositories.ProductRepository;

public class ProductService {
  private final ProductRepository productRepository;

  public ProductService (ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public ProductService () {
    this.productRepository = new ProductRepository();
  }

  public ObjectId insertProduct (
    String name,
    ObjectId categoryId,
    ObjectId supplierId,
    int stock,
    double price
  ) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name is required.");
    }
    if (categoryId == null) {
      throw new IllegalArgumentException("Category Id is required.");
    }
    if (supplierId == null) {
      throw new IllegalArgumentException("Supplier Id is required.");
    }
    if (stock < 0) {
      throw new IllegalArgumentException("Stock should be greater than 0.");
    }
    if (price < 0) {
      throw new IllegalArgumentException("Price should be greater than 0.");
    }

    Product product = new Product(name, supplierId, categoryId, stock, price);

    try {
      ObjectId result =  productRepository.insertProduct(product);
      return result;

    } catch (DatabaseException exception) {
      throw new ServiceException(
        "Error in saving the product." + exception.getMessage(), 
        exception
      );
    }
  }
}
