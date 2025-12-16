package com.pluralsight.data;

import org.springframework.stereotype.Component;
import com.pluralsight.models.ShoppingCart;
import com.pluralsight.models.ShoppingCartItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public interface ShoppingCartDao
{
    ShoppingCart getByUserId(Integer userId);
    Map<Integer, ShoppingCartItem> getCart(Integer userId);
    void addProduct(Integer userId, Integer productId, Integer quantity);
    void updateCart(Integer userId, Integer productId, ShoppingCartItem shoppingCartItem);
    //void deleteCart(Integer userId, ShoppingCart shoppingCart);
    List<ShoppingCartItem> getItemsByUserId(Integer userId);
    void clearCart(Integer userId);
    ShoppingCart findByUserId(int userId);

    void create(ShoppingCart cart);

    void update(ShoppingCart cart);

    void saveCartAndItems(ShoppingCart cart) throws SQLException;
}
