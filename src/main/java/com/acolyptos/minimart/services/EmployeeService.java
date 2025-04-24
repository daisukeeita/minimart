package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Employee;
import com.acolyptos.minimart.repositories.EmployeeRepository;

/*
 * Service class for managing Employee-related business logic before database operations.
 * This class provides validations methods before passing the data to EmployeeRepository class.
 *
*/
public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  /*
   * Initializes the EmployeeService and establishes the access to
   * EmployeeRepository.
   */
  public EmployeeService() {
    this.employeeRepository = new EmployeeRepository();
  }

  /*
   * Validates data that will be given and creates an Employee object before
   * passing it to EmployeeRepository
   *
   * @param name - The name of the employee to be inserted.
   * 
   * @param email - The email of the employee to be inserted.
   * 
   * @param userId - The userId of the employee to be inserted.
   * 
   * @return The unique ObjectId of the employee generated from
   * EmployeeRepository.
   *
   * @throws IllegalArgumentException if one of the expected data is null or
   * empty.
   *
   * @throw ServiceException if the service error occurs during creation.
   */
  public ObjectId createEmployee(String name, String email, ObjectId userId) {
    // Checks if one of the data is null or empty.
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("Name is required.");
    if (email == null || email.trim().isEmpty())
      throw new IllegalArgumentException("Email is required.");
    if (userId == null)
      throw new IllegalArgumentException("User ID is required.");

    Employee employee = new Employee(name, email, userId);

    try {
      ObjectId result = employeeRepository.insertEmployee(employee);
      System.out.println(employee.getName() + " Successfully added!");

      return result;
    } catch (DatabaseException exception) {
      System.err.println("Error in database: " + exception.getMessage());
      throw new ServiceException("Failed to create employee: " + exception.getMessage(), exception);
    }
  }

  public Employee getEmployeeByName (String name) throws Exception {
    try {
      Employee employee = employeeRepository.getEmployeeByName(name);
      return employee;
    } catch (Exception e) {
      // TODO: handle exception
      throw new Exception("Employee not found.");
    }
  }

  public Employee getEmployeeById (ObjectId id) throws Exception {
    try {
      Employee employee = employeeRepository.getEmployeeById(id);
      return employee;
    } catch (Exception e) {
      // TODO: handle exception
      throw new Exception("Employee not found.");
    }
  }
}
