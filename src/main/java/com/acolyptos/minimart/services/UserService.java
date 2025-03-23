package com.acolyptos.minimart.services;

import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.User;
import com.acolyptos.minimart.repositories.UserRepository;

import com.acolyptos.minimart.utilities.PasswordUtility;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.exceptions.AuthenticationException;

import org.bson.types.ObjectId;

public class UserService {
  private final UserRepository userRepository;

  public UserService() {
    this.userRepository = new UserRepository();
  }

  public ObjectId createUser(String username, String password, String role) {
    if (username == null || username.trim().isEmpty())
      throw new IllegalArgumentException("Username is required.");

    if (password == null || password.trim().isEmpty())
      throw new IllegalArgumentException("Password is required.");

    if (role == null)
      throw new IllegalArgumentException("Role is required.");

    if (userRepository.getUserByUsername(username) != null)
      throw new IllegalArgumentException("Username already exists.");

    String hashedPassword = PasswordUtility.hashPassword(password);

    User user = new User();
    user.setUsername(username);
    user.setPassword(hashedPassword);

    if (Role.EMPLOYEE.toString().equalsIgnoreCase(role)) {
      user.setRole(Role.EMPLOYEE);

    } else if (Role.MANAGER.toString().equalsIgnoreCase(role)) {
      user.setRole(Role.MANAGER);

    } else {
      throw new IllegalArgumentException("Invalid Role Specified.");
    }

    try {
      ObjectId result = userRepository.insertUser(user);
      System.out.println(user.getUsername() + " Succesfully added!");
      return result;

    } catch (DatabaseException e) {
      System.err.println("Error in service layer: " + e.getMessage());
      throw e;
    }

  }

  public User authenticateUser(String username, String password) {
    if (username == null)
      throw new IllegalArgumentException("Username is required.");
    if (password == null)
      throw new IllegalArgumentException("Password is required.");

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

    } catch (DatabaseException exception) {
      System.err.println("Error in authenticating user - " + exception.getMessage());
      throw new AuthenticationException("Error in Authentication: " + exception.getMessage());
    }
  }
}
