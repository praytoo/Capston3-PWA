package com.pluralsight.data;

import org.springframework.stereotype.Component;
import com.pluralsight.models.Product;

import java.math.BigDecimal;
import java.util.List;

@Component
public interface ProductDao
{
    List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory);
    List<Product> listByCategoryId(int categoryId);
    Product getById(int productId);
    Product create(Product product);
    void update(int productId, Product product);
    void delete(int productId);
}
