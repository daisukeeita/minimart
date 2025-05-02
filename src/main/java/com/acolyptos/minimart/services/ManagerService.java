package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Manager;
import com.acolyptos.minimart.repositories.ManagerRepository;

/*
 * service class for managing Manager-related business logic before database operations.
 * This class provides validation methods before passing the data to ManagerRepository class.
*/
public class ManagerService {
  private final ManagerRepository managerRepository;

  public ManagerService (ManagerRepository managerRepository) {
    this.managerRepository = managerRepository;
  }

  /*
   * Initializes the ManagerService and establishes access to EmployeeRepository.
   */
  public ManagerService() {
    this.managerRepository = new ManagerRepository();
  }

  /*
   * Validates data that will be given and creates a Manager object before passing
   * it to EmployeeRepository.
   *
   * @param name - The name of the manager to be inserted.
   * 
   * @param email - The email. of the manager to be inserted.
   * 
   * @param userId - The userId of the manager to be inserted.
   * 
   * @return The unique ObjectId of the manager generated from ManagerRepository.
   * 
   * @throws IllegalArgumentException if one the expected data is null or empty.
   * 
   * @throws ServiceException if the service error occurs during creation.
   */
  public ObjectId createManager(String name, String email, ObjectId userId) {
    // Checks if one of the data is null or empty.
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("Name is required.");
    if (email == null || email.trim().isEmpty())
      throw new IllegalArgumentException("Email is required.");
    if (userId == null)
      throw new IllegalArgumentException("User ID is required.");

    Manager manager = new Manager(name, email, userId);

    try {
      ObjectId result = managerRepository.insertManager(manager);
      System.out.println(manager.getName() + " Successfully added!");

      return result;
    } catch (DatabaseException exception) {
      System.err.println("Error in database: " + exception.getMessage());
      throw new ServiceException("Failed to create manager: " + exception.getMessage(), exception);
    }
  }

  public Manager getManagerByName (String name) throws Exception {
    try {
      Manager manager = managerRepository.getMangerbyName(name);
      return manager;
    } catch (Exception e) {
      // TODO: handle exception
      throw new Exception("Manager not found.");
    }
  }

  public Manager getManagerById (ObjectId id) throws Exception {
    try {
      Manager manager = managerRepository.getManagerById(id);
      return manager;
    } catch (Exception e) {
      // TODO: handle exception
      throw new Exception("Manager not found.");
    }
  }
}
