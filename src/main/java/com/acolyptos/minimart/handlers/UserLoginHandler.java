package com.acolyptos.minimart.handlers;

import com.acolyptos.minimart.models.User;
import com.acolyptos.minimart.services.UserService;
import com.acolyptos.minimart.exceptions.AuthenticationException;
import com.acolyptos.minimart.exceptions.DatabaseException;

import com.acolyptos.minimart.utilities.JwtUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.*;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/*
 * Handles user login requests via HTTP.
 * This class implements HttpHandler Interface.
 */
public class UserLoginHandler implements HttpHandler {
  private final UserService userService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public UserLoginHandler() {
    this.userService = new UserService();
  }

  /*
   * Handles HTTP requests and determines if it is a POST request.
   * Calls `handleLogin()` if valid, else send a 400 error response.
   *
   * @param exchange - The HTTP request/response object.
   *
   * @throws IOException if an input/output error occurs.
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      handleLogin(exchange);
    } else {
      sendResponse(exchange, 400, "Method Not Allowed.");
    }
  }

  private void handleLogin(HttpExchange exchange) throws IOException {
    try {
      // Parse incoming JSON request
      User userLoginRequest = objectMapper.readValue(exchange.getRequestBody(), User.class);

      // Authenticate the user
      User authenticatedUser = userService.authenticateUser(userLoginRequest.getUsername(),
          userLoginRequest.getPassword());

      // Prepare or Generate JWT Token
      String token = JwtUtility.generateToken(authenticatedUser.getId().toString(),
          authenticatedUser.getRole().toString());

      // Prepare a response
      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("message", "Login Successfull");
      responseMap.put("token", token);

      String jsonResponse = objectMapper.writeValueAsString(responseMap);
      sendResponse(exchange, 200, jsonResponse);

    } catch (IllegalArgumentException exception) {
      sendResponse(exchange, 400, "Missing Field: " + exception.getMessage());
    } catch (AuthenticationException exception) {
      sendResponse(exchange, 401, "Failed in Authenticating User: " + exception.getMessage());
    } catch (DatabaseException exception) {
      sendResponse(exchange, 500, "Internal Server Error: " + exception.getMessage());
    } catch (Exception exception) {
      sendResponse(exchange, 400, "Invalid Request: " + exception.getMessage());
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
