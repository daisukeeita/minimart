package com.acolyptos.minimart.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acolyptos.minimart.exceptions.DatabaseException;
import com.acolyptos.minimart.exceptions.ServiceException;
import com.acolyptos.minimart.models.Category;
import com.acolyptos.minimart.repositories.CategoryRepository;

public class CategoryService {
  private final CategoryRepository categoryRepository;
  private static final Logger log = 
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
      if (name == null) {
        log.warn("Category name was not provided.");
        throw new IllegalArgumentException(
          "Category name is required."
        );
      }

      if (details == null) {
        log.warn("Category details was not provided.");
        throw new IllegalArgumentException(
          "Details of the category is required."
        );
      } 

      categoryRepository.insertCategory(category);
      log.info("Category saved successfully.");
    } catch (DatabaseException exception) {
      log.error("Database Error: " + exception.getMessage());
      throw new ServiceException(
        "Failed to create category: " + exception.getMessage(),
        exception
      );
    }
  }
}
