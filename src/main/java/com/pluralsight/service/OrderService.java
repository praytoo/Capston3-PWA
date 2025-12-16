package com.pluralsight.service;

import com.pluralsight.models.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pluralsight.data.OrderDao;
import com.pluralsight.data.ShoppingCartDao;
import com.pluralsight.data.UserDao;
import com.pluralsight.models.Orders;
import com.pluralsight.models.ShoppingCartItem;
import com.pluralsight.models.User;

import java.util.List;

@Service
public class OrderService {
    private ShoppingCartDao shoppingCartDao;
    private OrderDao orderDao;
    private UserDao userDao;

    @Autowired
    public OrderService(ShoppingCartDao shoppingCartDao, OrderDao orderDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.orderDao = orderDao;
        this.userDao = userDao;
    }

    public Orders checkOutOrder(int user){
        List<ShoppingCartItem> cartItems = shoppingCartDao.getItemsByUserId(getId(user));
        if (cartItems.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }
        Integer orderId = orderDao.createOrder(getId(user));
        for (ShoppingCartItem item : cartItems){
            orderDao.addOrderLineItem(orderId, item);
        }
        shoppingCartDao.clearCart(getId(user));
        return null;
    }

    private Integer getId(int user) {
        return 0;
    }

}
