package com.acolyptos.minimart.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Supplier {

  @BsonId
  private ObjectId id;
  @BsonProperty("name")
  private String name;
  @BsonProperty("address")
  private String address;
  @BsonProperty("contactNumber")
  private String contactNumber;
  @BsonProperty("email")
  private String email;

  public Supplier () {}

  public Supplier (
    String name, 
    String address, 
    String contactNumber, 
    String email
  ) {
    this.name = name;
    this.address = address;
    this.contactNumber = contactNumber;
    this.email = email;
  }

  public ObjectId getId () { return id; }
  public void setId (ObjectId id) { this.id = id; }

  public String getName () { return name; }
  public void setName (String name) { this.name = name; }

  public String getAddress () { return address; }
  public void setAddress (String address) { this.address = address; }

  public String getContactNumber () { return contactNumber; }
  public void setContactNumber (String contactNumber) {
    this.contactNumber = contactNumber; 
  }

  public String getEmail () { return email; }
  public void setEmail (String email) { this.email = email; }

  @Override
  public String toString() {
    return "Supplier{" +
              "name=" + name + 
              ", address=" + address + 
              ", contactNumber=" + contactNumber + 
              ", email=" + email + 
           "}";
  }

  
}

