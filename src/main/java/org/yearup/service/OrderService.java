package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Orders;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

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

    //methods that the order dao implements

    public Orders checkOutOrder(User user){
        List<ShoppingCartItem> cartItems = shoppingCartDao.getItemsByUserId(user.getId());
        if (cartItems.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }
        Integer orderId = orderDao.createOrder(user.getId());
        for (ShoppingCartItem item : cartItems){
            orderDao.addOrderLineItem(orderId, item);
        }
        shoppingCartDao.clearCart(user.getId());
        return null;
    }

}
