# Mini Mart Inventory Management System

A simple Inventory Management System built in Java with MongoDB, focusing on manual HTTP server implementation and basic authentication using JWT.

## Features

- **_User authentication_**: Register and log in as employee. Manager role will be added in the future updates.
- **_Inventory Management_**: Manages Products, categorize items, and track stock levels (to be expanded).
- **_Supplier Management_**: Basic supplier information handling (to be expanded).
- **_Sales Tracking_**: Record sales transactions (to be expanded).

## Project Structure

```
src/main/java/com/acolyptos/minimart/
|- database/
|- exceptions/
|- handlers/
|- models/
|- repositories/
|- server/
|- services/
|- utilities/
|- Main.java
```

## Setup

### Configurations

1.  Creation of `.env` file in the root directory with the following content:

```.env
 URI=mondodb_connection_string
 SECRET_KEY=generated_secret_key
```

### Build and Run

1. Install dependency from `pom.xml`, if using Maven

```sh
 mvn clean install
```

2.  Running the application using Maven

```sh
 mvn exec:java -Dexec.mainClass="com.acolyptos.minimart.Main"
```

3. Or make a `build.sh` file to pseudo-automate the commands every to we call the file

```sh
#!/bin/bash

# Clean previous build files
mvn clean

# Compile the project
mvn compile

# Run the project quietly (only show println and errors)
mvn -q exec:java -Dexec.mainClass="com.acolyptos.minimart.Main"
```

```sh
chmod +x build.sh
```

```sh
./build.sh
```

## API Endpoints

### Register Employee

- **POST** `/api/register`
- **Request Body**:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "password": "securePassword",
  "role": "employee"
}
```

- **Response Body**:

```json
{
  "employeeId": "67d23929622cd64ddeac2356",
  "message": "User successfully Registered",
  "userId": "67d23928622cd64ddeac2355"
}
```

### Login Employee

- **POST** `/api/login`
- **Request Body**:

```json
{
  "username": "johndoe",
  "password": "securePassword"
}
```

- **Response Body**:

```json
{
  "message": "Login successful",
  "token": "<jwt-token>"
}
```

## Development Notes

- Exception handling is managed via custom exceptions
- Passwords are hashed using JBcrypt
- JWT is used for secure token-based authentication

## Future Developments

- Role based access control.
- Full CRUD for inventory, Supplier, and Sales.
- Manager registration and functionalities.
- API validation improvements.

# THIS PROJECT IS FOR LEARNING PURPOSES AND DOES NOT CARRY ANY FORMAL LICENSE.
