package com.acolyptos.minimart.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Product {

  @BsonId
  private ObjectId id;
  @BsonProperty("name")
  private String name;
  @BsonProperty("categoryId")
  private ObjectId categoryId;
  @BsonProperty("supplierId")
  private ObjectId supplierId;
  @BsonProperty("stock")
  private int stock;
  @BsonProperty("price")
  private double price;

  public Product () {}

  public Product (
    String name,
    ObjectId supplierId,
    ObjectId categoryId,
    int stock,
    double price
  ) {
    this.name = name;
    this.supplierId = supplierId;
    this.categoryId = categoryId;
    this.stock = stock;
    this.price = price;
  }

  public String getName () { return name; }
  public void setName (String name) { this.name = name; }

  public ObjectId getSupplierId () { return supplierId; }
  public void setSupplier (ObjectId supplierId) { this.supplierId = supplierId; }

  public ObjectId getCategoryId () { return categoryId; }
  public void setCategory (ObjectId categoryId) { this.categoryId = categoryId; }

  public int getStock () { return stock; }
  public void setStock (int stock) { this.stock = stock; }

  public double getPrice () { return price; }
  public void setPrice (double price) { this.price = price; }

  @Override
  public String toString() {
    return "Product{name=" + name + 
    ", categoryId=" + categoryId + 
    ", supplierId=" + supplierId + 
    ", stock=" + stock + 
    ", price=" + price +  
    "}";
  }

  
}
