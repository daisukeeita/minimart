package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.models.Employee;
import com.acolyptos.minimart.repositories.EmployeeRepository;

public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public EmployeeService() {
    this.employeeRepository = new EmployeeRepository();
  }

  public ObjectId createEmployee(String name, String email, ObjectId userId) {
    Employee employee = new Employee();
    employee.setName(name);
    employee.setEmail(email);
    employee.setUserId(userId);

    try {
      ObjectId result = employeeRepository.insertEmployee(employee);
      System.out.println(employee.getName() + " Successfully added!");
      return result;
    } catch (DatabaseException e) {
      System.err.println("Error in service layer: " + e.getMessage());
      throw e;
    }

  }
}
