package com.acolyptos.minimart.services;

import org.bson.types.ObjectId;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Supplier;
import com.acolyptos.minimart.repositories.SupplierRepository;

public class SupplierService {

  private final SupplierRepository supplierRepository;

  public SupplierService () {
    this.supplierRepository = new SupplierRepository();
  }

  public ObjectId createSupplier (
    String name, String address, String contactNumber, String email
  ) {
    Supplier supplier = new Supplier(name, address, contactNumber, email);

    try {
      ObjectId result = supplierRepository.insertSupplier(supplier);
      return result;
    } catch (DatabaseException exception) {
      System.err.println("Error in service layer: " + exception.getMessage());
      throw new ServiceException(
        "Supplier creation failed: " + exception.getMessage(), exception
      );
    }
  }

  public Supplier retrieveSupplier (String name) throws Exception {
    try {
      Supplier supplier = supplierRepository.getSupplierByName(name);
      return supplier;
    } catch (Exception e) {
      throw new Exception("Supplier not found");
    }
  }
}
