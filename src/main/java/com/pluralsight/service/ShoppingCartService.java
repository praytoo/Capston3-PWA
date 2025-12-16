package com.pluralsight.service;

import com.pluralsight.data.ShoppingCartDao;
import com.pluralsight.data.UserDao;
import com.pluralsight.models.ShoppingCart;
import com.pluralsight.models.ShoppingCartItem;
import com.pluralsight.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShoppingCartService {

    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private UserService userService;

    @Autowired
    public ShoppingCartService(ShoppingCartDao shoppingCartDao, UserDao userDao, UserService userService) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.userService = userService;
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
            cart.setItems(new HashMap<>());  // ← CHANGED to HashMap
            shoppingCartDao.create(cart);
        } else if (cart.getItems() == null) {
            cart.setItems(new HashMap<>());  // ← CHANGED to HashMap
        }

        return cart;
    }

    public ShoppingCart addProduct(int productId, int quantity, String username) throws SQLException {
        ShoppingCart cart = getOrCreateCart(username);

        // Get the items map
        Map<Integer, ShoppingCartItem> items = cart.getItems();

        // Check if product already exists in cart
        ShoppingCartItem item = items.get(productId);

        if (item == null) {
            // Product not in cart yet, create new item
            item = new ShoppingCartItem();
            item.setProductId(productId);
            item.setQuantity(quantity);
            items.put(productId, item);  // Add to map with productId as key
        } else {
            // Product already in cart, increase quantity
            item.setQuantity(item.getQuantity() + quantity);
        }

        // Save cart + items through DAO
        shoppingCartDao.saveCartAndItems(cart);

        return cart;
    }


    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        shoppingCartDao.update(cart);
        return cart;
    }

    public Map<Integer, ShoppingCartItem> getCart(Principal principal){
        String username = principal.getName();
        User user = userService.getByUserName(username);
        Integer userId = user.getId();
        return shoppingCartDao.getCart(userId);
    }
    public void updateCart(Integer productId, ShoppingCartItem shoppingCartItem, Principal principal){
        String username = principal.getName();
        User user = userService.getByUserName(username);
        Integer userId = user.getId();
        shoppingCartDao.updateCart(userId, productId, shoppingCartItem);
    }
    /*
    public void deleteCart(ShoppingCart shoppingCart, Principal principal){
        String username = principal.getName();
        User user = userService.getByUserName(username);
        Integer userId = user.getId();
        shoppingCartDao.deleteCart(userId, shoppingCart);
    }
    */
    public void clearCart(Principal principal){
        String username = principal.getName();
        User user = userService.getByUserName(username);
        Integer userId = user.getId();
        shoppingCartDao.clearCart(userId);
    }
}