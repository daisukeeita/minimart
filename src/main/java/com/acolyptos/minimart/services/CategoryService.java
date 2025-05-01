package com.acolyptos.minimart.services;

import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ResourceNotFoundException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Category;
import com.acolyptos.minimart.repositories.CategoryRepository;

public class CategoryService {
  private final CategoryRepository categoryRepository;
  private static final Logger LOG = 
    LoggerFactory.getLogger(CategoryService.class);

  public CategoryService () {
    this.categoryRepository =  new CategoryRepository();
  }

  public CategoryService (CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void insertCategory (Category category) {
    String name = category.getName();
    String details = category.getDetails();

    try {
      if (name == null || name.trim().isEmpty()) {
        LOG.warn("Category name was not provided.");
        throw new IllegalArgumentException(
          "Category name is required."
        );
      }

      if (details == null || details.trim().isEmpty()) {
        LOG.warn("Category details was not provided.");
        throw new IllegalArgumentException(
          "Details of the category is required."
        );
      } 

      categoryRepository.insertCategory(category);
      LOG.info("Category saved successfully.");

    } catch (DatabaseException exception) {
      LOG.error("Database Error: " + exception.getMessage());
      throw new ServiceException(
        "Failed to create category: " + exception.getMessage(),
        exception
      );
    }
  }

  public Category getCategoryById (ObjectId id) {
    if (id == null) {
      LOG.error("Object Id was not provided.");
      throw new IllegalArgumentException("Id is required.");
    }

    try {
      Category category = categoryRepository.getCategoryById(id);

      return category;
    } catch (ResourceNotFoundException exception) {
      LOG.error("Category was not found in the database");
      throw new ServiceException(
        "Category not found: " + exception.getMessage(), 
        exception
      );

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException(
        "Database Error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public Category getCategoryByName (String name) {
    if (name == null) {
      LOG.error("Name was not provided.");
      throw new IllegalArgumentException("Name is required.");
    }

    try {
      Category category = categoryRepository.getCategoryByName(name);

      return category;
    } catch (ResourceNotFoundException exception) {
      LOG.error("Category was not found in the database");
      throw new ServiceException(
        "Category not found: " + exception.getMessage(), 
        exception
      );

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException(
        "Database Error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public List<Category> getAllCategories () {
    try {
      List<Category> categories = categoryRepository.getAllCategories();

      return categories;

    } catch (ResourceNotFoundException exception) {
      LOG.error("Empty List: " + exception.getMessage());
      throw new ServiceException(
        "List is Empty: " + exception.getMessage(), 
        exception
      );

    } catch (DatabaseException exception) {
      LOG.error("Error in database: " + exception.getMessage());
      throw new ServiceException(
        "Database Error: " + exception.getMessage(), 
        exception
      );
    }
  }

  public boolean deleteCategory (ObjectId id) {
    if (id == null) {
      LOG.error("Object Id was not provided.");
      throw new IllegalArgumentException(
        "Id is required."
      );
    }

    try {
      return categoryRepository.deleteCategory(id);

    } catch (ResourceNotFoundException exception) {
      LOG.error("Missing Category:" + exception.getMessage());
      throw new ServiceException(
        "Missing Category: " + exception.getMessage(), 
        exception
      );

    } catch (DatabaseException exception) {
      LOG.error("Error in deleting query: " + exception.getMessage());
      throw new ServiceException(
        "Delete Execution Failed: " + exception.getMessage(), 
        exception
      );
    }
  }
}
