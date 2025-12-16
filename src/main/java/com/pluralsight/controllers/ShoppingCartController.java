package com.pluralsight.controllers;

import com.pluralsight.models.CartRequest;
import com.pluralsight.models.ShoppingCart;
import com.pluralsight.models.ShoppingCartItem;
import com.pluralsight.security.SecurityUtils;
import com.pluralsight.service.ProductService;
import com.pluralsight.service.ShoppingCartService;
import com.pluralsight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

        import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("cart")
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;
    private UserService userService;
    private ProductService productService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService, ProductService productService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.productService = productService;
    }
    // GET /cart - Get current user's cart
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
    // POST /cart/products/15?quantity=2 - Add product to cart (existing method)
    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductByPath(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal
    ) {
        shoppingCartService.addProduct(productId, quantity, principal);
        return buildCart(principal);
    }



    // POST /cart - Add product to cart (NEW - for your frontend)
    // This accepts JSON body: { "productId": 1, "quantity": 1 }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductByBody(
            @RequestBody AddToCartRequest request,
            Principal principal
    ) {
        Integer productId = request.getProductId();
        Integer quantity = request.getQuantity() != null ? request.getQuantity() : 1;

        shoppingCartService.addProduct(productId, quantity, principal);
        return buildCart(principal);
    }
    */

    // PUT /cart/products/15 - Update product quantity in cart
    @PutMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCart updateCart(
            @PathVariable Integer productId,
            @RequestBody ShoppingCartItem shoppingCartItem,
            Principal principal
    ) {
        shoppingCartService.updateCart(productId, shoppingCartItem, principal);
        return buildCart(principal);
    }

    // DELETE /cart - Clear entire cart
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCart clearCart(Principal principal) {
        shoppingCartService.clearCart(principal);
        return buildCart(principal);
    }

    // DELETE /cart/products/15 - Remove specific product from cart
    @DeleteMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCart removeProduct(@PathVariable Integer productId, Principal principal) {
        // You'll need to implement this in your service if not already there
        // shoppingCartService.removeProduct(productId, principal);
        return buildCart(principal);
    }

    // Helper method to build cart response
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

@PostMapping
public ShoppingCart addToCart(@RequestBody AddToCartRequest request) {
    String username = SecurityUtils.getCurrentUsername()
            .orElseThrow(() -> new RuntimeException("Not authenticated"));

    try {
        return shoppingCartService.addProduct(
                request.getProductId(),
                request.getQuantity() != null ? request.getQuantity() : 1,
                username
        );
    } catch (SQLException e) {
        throw new RuntimeException("Failed to add product to cart", e);
    }
}

    // DTO for request body
    public static class AddToCartRequest {
        private Integer productId;
        private Integer quantity;

        public AddToCartRequest() {
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}