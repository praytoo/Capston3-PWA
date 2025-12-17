package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.ShoppingCartItem;

@Component
public interface OrderDao {
    //methods to be overridden in OrderDaoImpl
    Integer createOrder(Integer userId);
    void addOrderLineItem(Integer orderId, ShoppingCartItem item);
}
