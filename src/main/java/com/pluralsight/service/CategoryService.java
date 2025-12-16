package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.validation.constraints.AssertFalse;
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
