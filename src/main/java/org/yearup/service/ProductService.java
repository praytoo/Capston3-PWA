package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    //methods that the product dao implements

    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory){
        return productDao.search(categoryId, minPrice, maxPrice, subCategory);
    }
    public List<Product> listByCategoryId(int categoryId){
        return productDao.listByCategoryId(categoryId);
    }
    public Product getById(int productId){
        return productDao.getById(productId);
    }
    public Product create(Product product){
        return productDao.create(product);
    }
    public void update(int productId, Product product){
        productDao.update(productId, product);
    }
    public void delete(int productId){
        productDao.delete(productId);
    }
}
