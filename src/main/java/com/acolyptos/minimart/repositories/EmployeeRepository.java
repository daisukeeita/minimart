package com.acolyptos.minimart.repositories;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDB;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.models.Employee;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

/*
 * Repository class for managing Employee-related database operations.
 * This class provides methods to insert, retrieve, and delete employees
 * from MongoDB database.
*/
public class EmployeeRepository {
  private final MongoCollection<Employee> employeeCollection;

  /*
   * Initializes the EmployeeRepository and establishes a connection to
   * the "employees" collection.
   */
  public EmployeeRepository() {
    DatabaseProvider mongoDB = new MongoDB();
    this.employeeCollection = mongoDB.getDatabase().getCollection("employees", Employee.class);
  }

  /*
   * Inserts new employee into the MongoDB collection.
   *
   * @param employee - The employee object to be inserted.
   * 
   * @return - The unique ObjectId of the inserted Employee.
   * 
   * @throws - DatabaseException if the database error occurs during insertion.
   */
  public ObjectId insertEmployee(Employee employee) {
    try {
      InsertOneResult result = employeeCollection.insertOne(employee);
      return result.getInsertedId().asObjectId().getValue();
    } catch (MongoWriteException exception) {
      // Handles issues like duplicate key errors or other data constraints
      System.err.println("Write Error: " + exception.getError().getMessage());
      throw new DatabaseException("Write Error: " + exception.getError().getMessage(), exception);

    } catch (MongoWriteConcernException exception) {
      // Handles issues related to write concerns (e.g., Failed Write Acknowledgement)
      System.err.println("Write Concern Error: " + exception.getMessage());
      throw new DatabaseException("Write concern error: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      // Catches other general MongoDB-related exceptions
      System.err.println("Database Error: " + exception.getMessage());
      throw new DatabaseException("MongoDB error: " + exception.getMessage(), exception);

    } catch (Exception exception) {
      // Catches any other Unexpected Runtime Exceptions
      System.err.println("Unexpected Error: " + exception.getMessage());
      throw new DatabaseException("Unexpected error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves an Employee from the database by their unique ObjectId.
   * 
   * @param id - The unique ObjectId of the Employee.
   *
   * @return The Employee object, if found.
   *
   * @throws ResourceNotFoundException if no employee is found with the given ID.
   *
   * @throws DatabaseException if an error occurs during the retrieval process.
   */
  public Employee getEmployeeById(ObjectId id) {
    try {
      Employee employee = employeeCollection
          .find(Filters.eq("_id", id))
          .first();

      if (employee == null) {
        throw new ResourceNotFoundException("Employee with id " + id + " not found");
      }

      return employee;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves an Employee from the database by their name.
   *
   * @param name - The name of the Employee.
   *
   * @return The Employee object, if found.
   *
   * @throws ResourceNotFoundException if no employee is found with the given
   * name.
   *
   * @throws DatabaseException if an error occurs during retrieval process.
   */
  public Employee getEmployeeByName(String name) {
    try {
      Employee employee = employeeCollection.find(Filters.eq("name", name)).first();

      if (employee == null) {
        throw new ResourceNotFoundException("Employee with name " + name + " not found");
      }

      return employee;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Retrieves all Employee documents stored in the database.
   * 
   * @return A list of Employee objects.
   *
   * @throws ResourceNotFoundException if no employee is found.
   *
   * @throws DatabaseException if an error occurs during the retrieval process.
   */
  public List<Employee> getAllEmployees() {
    List<Employee> employees = new ArrayList<>();

    // Checks if "employees" collection is not empty
    Employee checkIfEmpty = employeeCollection.find().first();
    if (checkIfEmpty == null)
      throw new ResourceNotFoundException("No employees found in the database.");

    try {
      // Iterates through the collection and adds all found employees to the list
      for (Employee employee : employeeCollection.find()) {
        employees.add(employee);
      }

      return employees;
    } catch (MongoQueryException exception) {
      throw new DatabaseException("Query Excecution Failed: " + exception.getMessage(), exception);

    } catch (MongoTimeoutException exception) {
      throw new DatabaseException("Database Timeout: " + exception.getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }

  /*
   * Deletes an Employee from the database using their unique ObjectId.
   *
   * @param id - The unique ObjectId of the Employee to be deleted.
   *
   * @return true if the deletion was successful.
   *
   * @throws ResourceNotFoundException if no employee is found with the given
   * ObjectId.
   *
   * @throws DatabaseException if error occurs during the deletion.
   */
  public boolean deleteEmployee(ObjectId id) {
    try {
      // Attemps to delete the employee and checks if the deletion was successful
      DeleteResult result = employeeCollection.deleteOne(Filters.eq("_id", id));

      // Checks if employee exists based on given unique ObjectId.
      if (result.getDeletedCount() == 0)
        throw new ResourceNotFoundException("Employee not found.");

      return true;
    } catch (MongoWriteConcernException exception) {
      throw new DatabaseException("Write Concern Failed: " + exception.getMessage(), exception);

    } catch (MongoWriteException exception) {
      throw new DatabaseException("Write Failed: " + exception.getError().getMessage(), exception);

    } catch (MongoException exception) {
      throw new DatabaseException("MongoDB Error: " + exception.getMessage(), exception);
    }
  }
}
