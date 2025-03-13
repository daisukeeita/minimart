package com.acolyptos.minimart.models;

import org.bson.types.ObjectId;

public class User {
  private ObjectId id;
  private String username;
  private String password;
  private Role role;

  public ObjectId getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return "User {" +
        "id: '" + id + '\'' +
        ", username: '" + username + '\'' +
        ", role: '" + role + '\'' +
        '}';
  }
}
