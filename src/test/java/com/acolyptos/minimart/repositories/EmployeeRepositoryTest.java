package com.acolyptos.minimart.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.acolyptos.minimart.database.DatabaseProvider;
import com.acolyptos.minimart.database.MongoDBTest;
import com.acolyptos.minimart.models.Employee;
import com.mongodb.client.MongoCollection;

class EmployeeRepositoryTest {
  static DatabaseProvider mongoConnection;
  static MongoCollection<Employee> employeeCollection;
  static EmployeeRepository employeeRepository;

  @BeforeEach
  void setup() {
    mongoConnection = new MongoDBTest();
    employeeCollection = mongoConnection
      .getDatabase().getCollection("employees_test", Employee.class);
    employeeRepository = new EmployeeRepository(employeeCollection);
    employeeCollection.drop();
  }

  @AfterEach
  void close() {
    mongoConnection.closeConnection();
  }

  @Test
  void insertAndFindEmployeeTest() {
    ObjectId userId = new ObjectId();
    Employee employee = new Employee("John Doe", "sample@mail.com", userId);

    ObjectId result = employeeRepository.insertEmployee(employee);
    assertNotNull(result, "Result should not be null upon inserting");

    Employee byName = employeeRepository
      .getEmployeeByName("John Doe");
    assertNotNull(byName, "Employee should not be null when fetched by Name");

    assertEquals("John Doe", byName.getName());
    assertEquals("sample@mail.com", byName.getEmail());
    assertEquals("userId", byName.getUserId());
  }
}
