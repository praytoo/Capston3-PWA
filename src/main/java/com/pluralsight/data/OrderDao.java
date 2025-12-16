package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.Orders;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.math.BigDecimal;

@Component
public interface OrderDao {
    Integer createOrder(Integer userId);
    void addOrderLineItem(Integer orderId, ShoppingCartItem item);
}
