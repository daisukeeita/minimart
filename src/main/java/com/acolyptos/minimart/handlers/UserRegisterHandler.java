package com.acolyptos.minimart.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.models.Role;
import com.acolyptos.minimart.models.UserRegistrationRequest;
import com.acolyptos.minimart.services.EmployeeService;
import com.acolyptos.minimart.services.ManagerService;
import com.acolyptos.minimart.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/*
 * Handles user registration requests via HTTP.
 * This class implements HttpHandler interface.
 */
/**
 * UserRegisterHandler
 */
public class UserRegisterHandler implements HttpHandler {
  private final UserService userService;
  private final EmployeeService employeeService;
  private final ManagerService managerService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private ObjectId employeeId;
  private ObjectId managerId;

  /*
   * Handles HTTP requests and determines if it is a POST request.
   * Calls `handleRegisterUser()` if valid, else return 400 error.
   *
   * @param exchange - The HTTP request/response object.
   * 
   * @throws IOException if an input/output error occurs.
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      handleRegisterUser(exchange);

    } else {
      sendResponse(exchange, 400, "Method not Allowed.");
    }
  }

  /*
   * Initializes the UserRegisterHandler and establishes the access to
   * UserService, EmployeeService, and ManagerService
   */
  public UserRegisterHandler() {
    this.userService = new UserService();
    this.employeeService = new EmployeeService();
    this.managerService = new ManagerService();
  }

  /*
   * Processes the user registration request and sends JSON response to the
   * client.
   *
   * @param exchange - The request/response object.
   * 
   * @throws IOException if an input/output error occurs.
   */
  private void handleRegisterUser(HttpExchange exchange) throws IOException {
    try {
      // Using a dedicated object instance to handle the request
      UserRegistrationRequest request = objectMapper.readValue(exchange.getRequestBody(),
          UserRegistrationRequest.class);

      // Split the value to the user and employee/manager then process these request
      ObjectId userId = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());

      if (request.getRole().equalsIgnoreCase(Role.EMPLOYEE.toString())) {
        employeeId = employeeService.createEmployee(request.getName(), request.getEmail(), userId);

      } else if (request.getRole().equalsIgnoreCase(Role.MANAGER.toString())) {
        managerId = managerService.createManager(request.getName(), request.getEmail(), userId);
      }

      // Prepare a response JSON to the client
      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("message", "User successfully created");
      responseMap.put("userId", userId.toHexString());

      if (request.getRole().equalsIgnoreCase(Role.EMPLOYEE.toString())) {
        responseMap.put("employeeId", employeeId.toHexString());

      } else if (request.getRole().equalsIgnoreCase(Role.MANAGER.toString())) {
        responseMap.put("managerId", managerId.toHexString());
      }

      String jsonResponse = objectMapper.writeValueAsString(responseMap);
      sendResponse(exchange, 201, jsonResponse);

    } catch (DatabaseException exception) {
      sendResponse(exchange, 500, "Internal Server Error: " + exception.getMessage());

    } catch (IllegalArgumentException exception) {
      sendResponse(exchange, 400, "Validation Error: " + exception.getMessage());

    } catch (JsonProcessingException exception) {
      sendResponse(exchange, 400, "Invalid JSON Format: " + exception.getMessage());

    } catch (Exception exception) {
      System.err.println("Unexpected error: " + exception.getMessage());
      sendResponse(exchange, 500, "Internal Server Error.");
    }
  }

  /*
   * Processes the JSON reponse content based on the result of the handler
   *
   * @params exchange - The request/response object
   *
   * @params statusCode - The code that will be set to the headers
   *
   * @params response - The body of the HTTP response that will be sent
   *
   * @throws IOException if the error in processing response occurs
   */
  private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }
}
