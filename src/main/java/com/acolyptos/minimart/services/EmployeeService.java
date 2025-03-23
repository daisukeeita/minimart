package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Employee;
import com.acolyptos.minimart.repositories.EmployeeRepository;

public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public EmployeeService() {
    this.employeeRepository = new EmployeeRepository();
  }

  public ObjectId createEmployee(String name, String email, ObjectId userId) {
    if (name == null || name.trim().isEmpty())
      throw new IllegalArgumentException("Name is required.");
    if (email == null || email.trim().isEmpty())
      throw new IllegalArgumentException("Email is required.");
    if (userId == null)
      throw new IllegalArgumentException("userId is required.");

    Employee employee = new Employee(name, email, userId);

    try {
      ObjectId result = employeeRepository.insertEmployee(employee);
      System.out.println(employee.getName() + " Successfully added!");
      return result;

    } catch (DatabaseException exception) {
      System.err.println("Error in database: " + exception.getMessage());
      throw new ServiceException("Failed to create employee", exception);
    }
  }
}
