package com.pluralsight.data;

import org.springframework.stereotype.Component;
import com.pluralsight.models.ShoppingCartItem;

@Component
public interface OrderDao {
    Integer createOrder(Integer userId);
    void addOrderLineItem(Integer orderId, ShoppingCartItem item);
}
