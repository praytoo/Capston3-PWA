package com.pluralsight.data;

import org.springframework.stereotype.Component;
import com.pluralsight.models.Category;

import java.util.List;

@Component
public interface CategoryDao
{
    List<Category> getAllCategories(String name);
    Category getById(int categoryId);
    Category create(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
