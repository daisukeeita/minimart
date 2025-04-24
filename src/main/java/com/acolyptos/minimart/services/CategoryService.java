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

  public void insertCategory (Category category) {
    String name = category.getName();
    String details = category.getDetails();

    try {
      if (name == null) {
        throw new IllegalArgumentException(
          "Category name is required."
        );
      }

      if (details == null) {
        throw new IllegalArgumentException(
          "Details of the category is required."
        );
      } 

      log.info("Category saved successfully.");
      categoryRepository.insertCategory(category);
    } catch (DatabaseException exception) {
      log.error("Database Error: " + exception.getMessage());
      throw new ServiceException(
        "Failed to create category: " + exception.getMessage(),
        exception
      );
    }
  }
}
