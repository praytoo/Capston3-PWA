package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ProductService;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

// convert this class to a REST controller
// only logged-in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("permitAll()")
@CrossOrigin
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartService shoppingCartService;
    private UserService userService;
    private ProductService productService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService, ProductService productService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.productService = productService;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        Map<Integer, ShoppingCartItem> items = shoppingCartService.getCart(principal);

        BigDecimal total = items.values().stream()
                .map(i ->
                        i.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        cart.setTotal(total);

        return cart;
    }


    /*
    // add a POST method to add a product to the cart - the url should be
    // http://localhost:8080/cart/products/15 (15 is the productId to be added
    //POST http://localhost:8080/cart/products/15?quantity=2
    @PostMapping("/products/{productId}")
    public void addProduct(@PathVariable Integer productId, @RequestParam(defaultValue = "1") Integer quantity, Principal principal) {
        shoppingCartService.addProduct(productId, quantity, principal);
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // http://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{productId}")
    public void updateCart(@PathVariable Integer productId, @RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        shoppingCartService.updateCart(productId, shoppingCartItem, principal);
    }
    // add a DELETE method to clear all products from the current users cart
    // http://localhost:8080/cart
    @DeleteMapping
    public void deleteCart(ShoppingCart shoppingCart, Principal principal){
        shoppingCartService.deleteCart(shoppingCart, principal);
    }
     */

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProduct2(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal
    ) {
        shoppingCartService.addProduct(productId, quantity, principal);

        Map<Integer, ShoppingCartItem> items = shoppingCartService.getCart(principal);

        BigDecimal total = items.values().stream()
                .map(i ->
                        i.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        cart.setTotal(total);

        return cart;
    }


    @PutMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCart updateCart2(@PathVariable Integer productId, @RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        shoppingCartService.updateCart(productId, shoppingCartItem, principal);
        Map<Integer, ShoppingCartItem> items = shoppingCartService.getCart(principal);

        BigDecimal total = items.values().stream()
                .map(i ->
                        i.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        cart.setTotal(total);

        return cart;
    }

    @DeleteMapping
    //@ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCart deleteCart2(Principal principal) {
        shoppingCartService.clearCart(principal);
        Map<Integer, ShoppingCartItem> items = shoppingCartService.getCart(principal);

        BigDecimal total = items.values().stream()
                .map(i ->
                        i.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        cart.setTotal(total);

        return cart;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ShoppingCart buildCart(Principal principal) {
        Map<Integer, ShoppingCartItem> items = shoppingCartService.getCart(principal);

        BigDecimal total = items.values().stream()
                .map(i -> i.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        cart.setTotal(total);

        return cart;
    }
}