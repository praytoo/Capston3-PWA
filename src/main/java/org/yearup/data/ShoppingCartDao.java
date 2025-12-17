package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;
import java.util.Map;

@Component
public interface ShoppingCartDao
{
    //methods to be overridden in ShoppingCartDaoImpl
    ShoppingCart getByUserId(Integer userId);
    // add additional method signatures here
    Map<Integer, ShoppingCartItem> getCart(Integer userId);
    void addProduct(Integer userId, Integer productId, Integer quantity);
    void updateCart(Integer userId, Integer productId, ShoppingCartItem shoppingCartItem);
    //void deleteCart(Integer userId, ShoppingCart shoppingCart);
    List<ShoppingCartItem> getItemsByUserId(Integer userId);
    void clearCart(Integer userId);
}
