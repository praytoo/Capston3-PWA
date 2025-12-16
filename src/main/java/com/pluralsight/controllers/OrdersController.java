package com.pluralsight.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pluralsight.models.Orders;
import com.pluralsight.models.User;
import com.pluralsight.service.OrderService;
import com.pluralsight.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
public class OrdersController {
    private OrderService orderService;
    private UserService userService;

    @Autowired
    public OrdersController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public Orders checkOutOrder(Principal principal) {
        String username = principal.getName();
        User user = userService.getByUserName(username);
        return orderService.checkOutOrder(user.getId());
    }
}
