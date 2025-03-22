package com.acolyptos.minimart.services;

import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.User;
import com.acolyptos.minimart.repositories.UserRepository;

import com.acolyptos.minimart.utilities.PasswordUtility;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.AuthenticationException;

import org.bson.types.ObjectId;

public class UserService {
  private final UserRepository userRepository;

  public UserService() {
    this.userRepository = new UserRepository();
  }

  public ObjectId createUser(String username, String password, Role role) {
    String hashedPassword = PasswordUtility.hashPassword(password);

    User user = new User();
    user.setUsername(username);
    user.setPassword(hashedPassword);
    user.setRole(role);

    try {
      ObjectId result = userRepository.insertUser(user);

      System.out.println(user.getUsername() + " Succesfully added!");
      return result;
    } catch (DatabaseException e) {
      System.err.println("Error in sevice layer: " + e.getMessage());
      throw e;
    }

  }

  public User authenticateUser(String username, String password) {
    try {
      User user = userRepository.getUserByUsername(username);

      if (user == null) {
        throw new AuthenticationException("User not found.");
      }

      if (!PasswordUtility.checkPassword(password, user.getPassword().toString())) {
        throw new AuthenticationException("Invalid Password.");
      }

      System.out.println("User logged in Succesfully.");
      return user;

    } catch (AuthenticationException e) {
      System.err.println("Error in authenticating user - " + e.getMessage());
      throw e;
    }
  }
}
