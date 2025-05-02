package com.acolyptos.minimart.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bson.types.ObjectId;
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

  public ProductRegisterHandler(
    CategoryService categoryService, 
    ProductService productService, 
    SupplierService supplierService
  ) {
    this.categoryService = Objects.requireNonNull(categoryService, "Category Service cannot be null.");
    this.productService = Objects.requireNonNull(productService, "Product Service cannot be null.");
    this.supplierService = Objects.requireNonNull(supplierService, "Supplier Service cannot be null.");
  }

  public ProductRegisterHandler () {
    this.categoryService = new CategoryService();
    this.productService = new ProductService();
    this.supplierService = new SupplierService();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      handleAddProduct(exchange);
    } else {
      sendResponse(exchange, 405, "Method not Allowed. Expected POST.");
    }
  }

  private void handleAddProduct(HttpExchange exchange) throws IOException {

    try {
      ProductRegistrationRequest requestProduct = objectMapper.readValue(
        exchange.getRequestBody(), 
        ProductRegistrationRequest.class
      );

      validateInputRequest(
        requestProduct.getName(),
        requestProduct.getCategoryName(), 
        requestProduct.getSupplierName(),
        requestProduct.getStock(), 
        requestProduct.getPrice()
      );


      Category category = categoryService.getCategoryByName(requestProduct.getCategoryName());
      if (category == null) {
        sendResponse(exchange, 400, "Category not found.");
        return;
      }

      Supplier supplier = supplierService.getSupplierByName(requestProduct.getSupplierName());
      if (supplier == null) {
        sendResponse(exchange, 400, "Supplier not found.");
        return;
      }

      ObjectId productId =  productService.insertProduct(
        requestProduct.getName(), 
        category.getId(), 
        supplier.getId(),
        requestProduct.getStock(), 
        requestProduct.getPrice()
      );

      if (productId == null) {
        sendResponse(exchange, 400, "Product was not created successfully.");
        return;
      } else {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Product " + requestProduct.getName() + " successfully created.");
        responseMap.put("productId", productId);
        String jsonResponse = objectMapper.writeValueAsString(responseMap);

        sendResponse(exchange, 201, jsonResponse);
    }

 
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

  private void validateInputRequest(
    String name, 
    String categoryName, 
    String supplierName,
    int stock, 
    double price
  ) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Product Name was not provided.");
    } 
    if (categoryName == null || categoryName.trim().isEmpty()) {
      throw new IllegalArgumentException("Category Name was not provided.");
    } 
    if (supplierName == null || supplierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Supplier Name was not provided.");
    } 
    if (stock <= 0) {
      throw new IllegalArgumentException("Stock number shouldn't be lower than or equals to 0.");
    } 
    if (price <= 0) {
      throw new IllegalArgumentException("Price number shouldn't be lower than or equals to 0.");
    } 
  }
}
