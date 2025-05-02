package com.acolyptos.minimart.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.models.Category;
import com.acolyptos.minimart.models.ProductRegistrationRequest;
import com.acolyptos.minimart.models.Supplier;
import com.acolyptos.minimart.services.CategoryService;
import com.acolyptos.minimart.services.ProductService;
import com.acolyptos.minimart.services.SupplierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;



public class ProductRegisterHandler implements HttpHandler {
  private final ProductService productService;
  private final CategoryService categoryService;
  private final SupplierService supplierService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProductRegisterHandler() {
    this.productService = new ProductService();
    this.categoryService = new CategoryService();
    this.supplierService = new SupplierService();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      handleAddProduct(exchange);
    } else {
      sendResponse(exchange, 400, "Method not Allowed.");
    }
  }

  private void handleAddProduct(HttpExchange exchange) throws IOException {
    Category category;
    Supplier supplier;
    ProductRegistrationRequest requestProduct;

    try {
      requestProduct =
          objectMapper.readValue(exchange.getRequestBody(), ProductRegistrationRequest.class);

      boolean validateInputRequest = validateInputRequest(requestProduct.getName(),
          requestProduct.getCategoryName(), requestProduct.getSupplierName(),
          requestProduct.getStock(), requestProduct.getPrice());


      if (validateInputRequest == false) {
        return;
      } else {
        category = categoryService.getCategoryByName(requestProduct.getName());
        supplier = supplierService.getSupplierByName(requestProduct.getName());

        productService.insertProduct(requestProduct.getName(), category.getId(), supplier.getId(),
            requestProduct.getStock(), requestProduct.getPrice());
      }

      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("message", "Product " + requestProduct.getName() + " successfully created.");
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

  private void sendResponse(HttpExchange exchange, int statusCode, String response)
      throws IOException {

    byte[] responseByte = response.getBytes(StandardCharsets.UTF_8);

    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, responseByte.length);

    try (OutputStream os = exchange.getResponseBody()) {
      os.write(responseByte);
    }
  }

  private boolean validateInputRequest(String name, String categoryName, String supplierName,
      int stock, double price) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Product Name was not provided.");
    } else if (categoryName == null || categoryName.trim().isEmpty()) {
      throw new IllegalArgumentException("Category Name was not provided.");
    } else if (supplierName == null || supplierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Supplier Name was not provided.");
    } else if (stock <= 0) {
      throw new IllegalArgumentException("Stock number shouldn't be lower than or equals to 0.");
    } else if (price <= 0) {
      throw new IllegalArgumentException("Price number shouldn't be lower than or equals to 0.");
    } else {
      return true;
    }

  }
}
