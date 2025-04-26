package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Supplier;
import com.acolyptos.minimart.repositories.SupplierRepository;

public class SupplierService {

  private final SupplierRepository supplierRepository;
  private final Logger LOG = LoggerFactory.getLogger(SupplierService.class);

  public SupplierService () {
    this.supplierRepository = new SupplierRepository();
  }

  public SupplierService (SupplierRepository supplierRepository) {
    this.supplierRepository = supplierRepository;
  }

  public ObjectId createSupplier (
    String name, 
    String address, 
    String contactNumber, 
    String email
  ) {

    if (name == null || name.trim().isEmpty()) {
      LOG.error("Name was not provided.");
      throw new IllegalArgumentException("Name is required.");
    }

    if (address == null || address.trim().isEmpty()) {
      LOG.error("Address was not provided.");
      throw new IllegalArgumentException("Address is required.");
    }

    if (contactNumber == null || contactNumber.trim().isEmpty()) {
      LOG.error("Contact Number was not provided.");
      throw new IllegalArgumentException("Contact Number is required.");
    }

    if (email == null || email.trim().isEmpty()) {
      LOG.error("Email was not provided.");
      throw new IllegalArgumentException("Email is required.");
    }

    Supplier supplier = new Supplier(name, address, contactNumber, email);

    try {
      ObjectId result = supplierRepository.insertSupplier(supplier);
      return result;

    } catch (DatabaseException exception) {
      LOG.error("Error in creating supplier -> " + exception.getMessage());
      throw new ServiceException(
        "Supplier creation failed: " + exception.getMessage(), exception
      );
    }
  }

  public Supplier getSupplierByName (String name) {
    
    if (name == null || name.trim().isEmpty()) {
      LOG.error("Name was not provided.");
      throw new IllegalArgumentException("Name is required.");
    }

    try {
      Supplier supplier = supplierRepository.getSupplierByName(name);
      return supplier;

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException(
        "Database Error: " + exception.getMessage(), exception
      );

    } catch (ResourceNotFoundException exception) {
      LOG.error("Error in retrieving supplier: " + exception.getMessage());
      throw new ServiceException(
        "Retrieval Error: " + exception.getMessage(), exception
      );
    }
  }

  public Supplier getSupplierById (ObjectId id) {

    if (id == null) {
      LOG.error("Object Id was not provided.");
      throw new IllegalArgumentException("Id is required.");
    }

    try {
      Supplier supplier = supplierRepository.getSupplierById(id);
      return supplier;

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException("Database Error: ", exception);

    } catch (ResourceNotFoundException exception) {
      LOG.error("Error in retrieving supplier: " + exception.getMessage());
      throw new ServiceException("Retrieval Error: ", exception);
    }
  }

  public boolean deleteSupplier (ObjectId id) {

    if (id == null) {
      LOG.error("Id was not provided.");
      throw new IllegalArgumentException("Object Id is required.");
    }

    try {
      Boolean result = supplierRepository.deleteSupplier(id);
      return result;

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException(
        "Database Error: " + exception.getMessage(), exception
      );

    } catch (ResourceNotFoundException exception) {
      LOG.error("Unsuccessful in looking to supplier: " + exception.getMessage());
      throw new ServiceException(
        "Deletion Error: " + exception.getMessage(), exception
      );
    }
  }
}
