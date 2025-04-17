package com.acolyptos.minimart.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.bson.Document;
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
  static MongoCollection<Document> employeeCollection;
  static EmployeeRepository employeeRepository;

  @BeforeEach
  void setup() {
    mongoConnection = new MongoDBTest();
    employeeCollection = mongoConnection
      .getDatabase().getCollection("employees_test");
    // employeeRepository = new EmployeeRepository(employeeCollection);
  }

  @AfterEach
  void close() {
    mongoConnection.closeConnection();
  }

  @Test
  void insertAndFindEmployeeTest() {
    ObjectId userId = new ObjectId();
    Employee employee = new Employee("John Doe", "sample@mail.com", userId);
    Document doc = new Document("name", employee.getName())
      .append("email", employee.getEmail())
      .append("userId", employee.getUserId());

    ObjectId id = employeeCollection.insertOne(doc)
      .getInsertedId()
      .asObjectId()
      .getValue();

    assertNotNull(id, "ID should not be null");

  }
}
