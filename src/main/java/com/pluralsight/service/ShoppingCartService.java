package com.pluralsight.service;

import com.pluralsight.data.ShoppingCartDao;
import com.pluralsight.data.UserDao;
import com.pluralsight.models.ShoppingCart;
import com.pluralsight.models.ShoppingCartItem;
import com.pluralsight.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class ShoppingCartService {

    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartService(ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    public ShoppingCart getOrCreateCart(String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        ShoppingCart cart = shoppingCartDao.findByUserId(user.getId());

        if (cart == null) {
            cart = new ShoppingCart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            shoppingCartDao.create(cart);
        } else if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return cart;
    }
/*
    public ShoppingCart addProduct(int productId, int quantity, String username) throws SQLException {
        ShoppingCart cart = getOrCreateCart(username);

        ShoppingCartItem item = null;
        // Loop through items to find if it already exists
        for (ShoppingCartItem i : cart) {
            if (i.getProductId() == productId) {
                item = i;
                break;
            }
        }

        // If not found, create a new item
        if (item == null) {
            item = new ShoppingCartItem();
            item.setProductId(productId);
            item.setQuantity(0);
            cart.getItems().add(item);
        }

        // Update quantity
        item.setQuantity(item.getQuantity() + quantity);

        // Save cart + items through DAO
        shoppingCartDao.saveCartAndItems(cart);

        return cart;
    }

 */

    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        shoppingCartDao.update(cart);
        return cart;
    }
}