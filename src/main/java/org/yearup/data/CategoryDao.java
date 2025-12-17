package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.Category;

import java.util.List;

@Component
public interface CategoryDao
{
    //methods to be overridden in CategoryDaoImpl
    List<Category> getAllCategories(String name);
    Category getById(int categoryId);
    Category create(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
