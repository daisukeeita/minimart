package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.models.Employee;

import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class EmployeeRepository {
  private final MongoCollection<Employee> employeeCollection;

  public EmployeeRepository() {
    this.employeeCollection = MongoDB.getDatabase().getCollection("employees", Employee.class);
  }

  /*
   * Inserts new employee into the MongoDB collection
   *
   * @param employee - The employee object to be inserted
   * 
   * @return inserted unique identifier of the Employee
   */
  public ObjectId insertEmployee(Employee employee) {
    try {
      InsertOneResult result = employeeCollection.insertOne(employee);
      return result.getInsertedId().asObjectId().getValue();
    } catch (Exception e) {
      throw new DatabaseException("Failed to insert Employee - " + employee.getName(), e);
    }
  }

  /*
   * Retrieves an Employee by their Unique ID
   * 
   * @param id The unique identifier of the employee
   * 
   * @return The employee object if found, or null if not found.
   */
  public Employee getEmployeeById(ObjectId id) {
    try {
      Employee employee = employeeCollection.find(Filters.eq("_id", id)).first();
      if (employee == null) {
        throw new ResourceNotFoundException("Employee with id " + id + " not found");
      }
      return employee;

    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrieving Employee - " + e.getMessage(), e);
    }
  }

  /*
   * Retrieves and Employee by their name
   *
   * @param name The identifier of the employee
   * 
   * @return The employee object if found, or null if not found.
   */
  public Employee getEmployeeByName(String name) {
    try {
      Employee employee = employeeCollection.find(Filters.eq("name", name)).first();
      if (employee == null) {
        throw new ResourceNotFoundException("Employee with name " + name + " not found");
      }
      return employee;
    } catch (ResourceNotFoundException e) {
      throw new DatabaseException("Error retrieving Employee - " + e.getMessage(), e);
    }
  }

  /*
   * Retrieves all the Employess
   * 
   * @return The list of employees if found, or null if not found
   */
  public List<Employee> getAllEmployees() {
    List<Employee> employees = new ArrayList<>();

    try {
      for (Employee employee : employeeCollection.find()) {
        employees.add(employee);
      }

      return employees;
    } catch (Exception e) {
      throw new DatabaseException("List was empty.", e);
    }
  }

  /*
   * Deletes an employee from the collection by their ID
   * 
   * @param id - The unique identifier of the employee to be deleted
   * 
   * @return true if the deletion was successfull, false otherwise.
   */
  public boolean deleteEmployee(ObjectId id) {
    try {
      DeleteResult result = employeeCollection.deleteOne(Filters.eq("_id", id));

      return result.getDeletedCount() > 0;
    } catch (Exception e) {
      throw new DatabaseException("Failed to delete Employee.", e);
    }
  }
}
