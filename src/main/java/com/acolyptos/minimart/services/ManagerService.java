package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.models.Manager;
import com.acolyptos.minimart.repositories.ManagerRepository;
import com.acolyptos.minimart.exceptions.DatabaseException;

public class ManagerService {
  private final ManagerRepository managerRepository;

  public ManagerService() {
    this.managerRepository = new ManagerRepository();
  }

  public ObjectId createManager(String name, String email, ObjectId userId) {
    Manager manager = new Manager();
    manager.setName(name);
    manager.setEmail(email);
    manager.setUserId(userId);

    try {
      ObjectId result = managerRepository.insertManager(manager);

      System.out.println(manager.getName() + " Successfully added!");
      return result;
    } catch (Exception e) {
      System.out.println("Error in service layer: " + e.getMessage());
      throw new DatabaseException("Error: " + e.getMessage(), e);
    }
  }
}
