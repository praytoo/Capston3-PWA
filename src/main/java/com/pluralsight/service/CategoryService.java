package com.pluralsight.service;

import com.pluralsight.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pluralsight.data.CategoryDao;
import com.pluralsight.models.Category;

import java.util.List;

@Service
public class CategoryService {
    private CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<Category> getAllCategories(String name){
        return categoryDao.getAllCategories(name);
    }
    public Category getById(int categoryId){
        return categoryDao.getById(categoryId);
    }
    public Category create(Category category){
        return categoryDao.create(category);
    }
    public void update(int categoryId, Category category){
        categoryDao.update(categoryId, category);
    }
    public void delete(int categoryId){
        categoryDao.delete(categoryId);
    }
}
