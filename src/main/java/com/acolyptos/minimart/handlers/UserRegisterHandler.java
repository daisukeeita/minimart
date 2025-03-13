package com.acolyptos.minimart.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.User;
import com.acolyptos.minimart.models.UserRegistrationRequest;
import com.acolyptos.minimart.services.EmployeeService;
import com.acolyptos.minimart.services.UserService;
import com.acolyptos.minimart.utilities.PasswordUtility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRegisterHandler implements HttpHandler {
  private final UserService userService;
  private final EmployeeService employeeService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private ObjectId employeeId;
  private ObjectId managerId;

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      handleRegisterUser(exchange);
    } else {
      sendResponse(exchange, 400, "Method not Allowed.");
    }
  }

  public UserRegisterHandler() {
    this.userService = new UserService();
    this.employeeService = new EmployeeService();
  }

  private void handleRegisterUser(HttpExchange exchange) throws IOException {
    try {
      // Using a dedicated object instance to handle the request
      UserRegistrationRequest request = objectMapper.readValue(exchange.getRequestBody(),
          UserRegistrationRequest.class);

      // Split the value to the user and employee/manager then process these request
      User user = new User();
      user.setUsername(request.getUsername());
      user.setPassword(PasswordUtility.hashPassword(request.getPassword()));
      if ("employee".equalsIgnoreCase(request.getRole())) {
        user.setRole(Role.EMPLOYEE);
      } else if ("manager".equalsIgnoreCase(request.getRole())) {
        user.setRole(Role.MANAGER);
      }

      ObjectId savedUser = userService.createUser(user.getUsername(), user.getPassword(), user.getRole());

      if (user.getRole() == Role.EMPLOYEE) {
        employeeId = employeeService.createEmployee(request.getName(), request.getEmail(), savedUser);
      } // TODO: Add managerService after making a model and repository

      // Prepare a response JSON to the client
      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("message", "User successfully created");
      responseMap.put("userId", savedUser.toHexString());
      if (user.getRole() == Role.EMPLOYEE) {
        responseMap.put("employeeId", employeeId.toHexString());
      }
      // TODO:
      // Make an objectId return value for createManager()
      // method and add it as response to the client

      String jsonResponse = objectMapper.writeValueAsString(responseMap);
      sendResponse(exchange, 201, jsonResponse);
    } catch (DatabaseException e) {
      sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
    } catch (Exception e) {
      sendResponse(exchange, 400, "Invalid request: " + e.getMessage());
    }
  }

  private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }
}
