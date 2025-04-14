package com.acolyptos.minimart.services;

import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.User;
import com.acolyptos.minimart.repositories.UserRepository;

import com.acolyptos.minimart.utilities.PasswordUtility;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.exceptions.AuthenticationException;

import org.bson.types.ObjectId;

/*
 * Service class for managing User-related business logic before database operations.
 * This class provides validation methods before passing the data to UserRepository.
*/
public class UserService {
  private final UserRepository userRepository;

  /*
   * Initializes the UserService and establishes the access to UserRepository.
   */
  public UserService() {
    this.userRepository = new UserRepository();
  }

  /*
   * Validates data that will be given and creates a User object before passing it
   * to UserRepository.
   *
   * @param username - The username of the user to be inserted.
   * 
   * @param password - The password of the user to be inserted.
   * 
   * @param role - The role of the user to be inserted.
   * 
   * @return The unique ObjectId of the user generated from UserRepository
   *
   * @throws IllegalArgumentException if one of the expected data is null or
   * empty.
   *
   * @throws AuthenticationException if the user already exists upon checking.
   * 
   * @throws ServiceException if the service error occurs during creation.
   */
  public ObjectId createUser(String username, String password, String role) {
    if (username == null || username.trim().isEmpty())
      throw new IllegalArgumentException("Username is required.");

    if (password == null || password.trim().isEmpty())
      throw new IllegalArgumentException("Password is required.");

    if (role == null)
      throw new IllegalArgumentException("Role is required.");

    if (userRepository.getUserByUsername(username) != null)
      throw new AuthenticationException("Username already exists.");

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
    } catch (DatabaseException exception) {
      System.err.println("Error in service layer: " + exception.getMessage());
      throw new ServiceException("User creation failed: " + exception.getMessage(), exception);
    }

  }

  /*
   * Validates username and password and creates a User object.
   * 
   * @param username - The username of the user to be validated.
   * 
   * @param password - The password of the user to be validated.
   * 
   * @return The User object, if both username and password have been
   * authenticated.
   *
   * @throws IllegalArgumentException if one of the expected data is null or
   * empty.
   * 
   * @throws AuthenticationException if user not found by the given username.
   * 
   * @throws ServiceException if the service error occurs during Authentication.
   */
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
      throw new ServiceException("Error in Authentication: " + exception.getMessage(), exception);
    }
  }
}
