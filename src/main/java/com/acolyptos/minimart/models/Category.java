package com.acolyptos.minimart.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Category {
  @BsonId
  private ObjectId id;
  @BsonProperty("name")
  private String name;
  @BsonProperty("details")
  private String details;

  public Category () {}

  public Category (String name, String details) {
    this.name = name;
    this.details = details;
  }

  public String getName () { return name; }
  public void setName (String name) { this.name = name; }

  public String getDetails () { return details; }
  public void setDetails (String details) { this.details = details; }

  @Override
  public String toString() {
    return "Category{name=" + name + ", details=" + details + "}";
  }
}
