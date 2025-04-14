package com.acolyptos.minimart.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class User {
  @BsonId
  private ObjectId id;
  @BsonProperty("username")
  private String username;
  @BsonProperty("password")
  private String password;
  @BsonProperty("role")
  private Role role;

  public User() {

  }

  public User(String username, String password, Role role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

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
