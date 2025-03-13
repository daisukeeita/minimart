package com.acolyptos.minimart.models;

import org.bson.types.ObjectId;

public class Manager {
  private ObjectId id;
  private String name;
  private String email;
  private ObjectId userId;

  public ObjectId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String email() {
    return email;
  }

  public ObjectId getUserId() {
    return userId;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setUserId(ObjectId userId) {
    this.userId = userId;
  }
}
