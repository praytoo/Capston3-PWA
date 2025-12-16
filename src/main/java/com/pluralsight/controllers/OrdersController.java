package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.Orders;
import org.yearup.models.User;
import org.yearup.service.OrderService;
import org.yearup.service.UserService;

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
        return orderService.checkOutOrder(user);
    }
}
