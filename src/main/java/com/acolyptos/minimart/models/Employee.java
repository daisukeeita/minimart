package com.acolyptos.minimart.models;

import org.bson.types.ObjectId;

public class Employee {
  private ObjectId id;
  private String name;
  private String email;
  private ObjectId userId;

  public Employee(String name, String email, ObjectId userId) {
    this.name = name;
    this.email = email;
    this.userId = userId;
  }

  public ObjectId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
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

  @Override
  public String toString() {
    return "Employee {" +
        "id = '" + id + '\'' +
        ", name = '" + name + '\'' +
        ", email = '" + email + '\'' +
        ", userId = '" + userId + '\'' +
        '}';
  }
}
