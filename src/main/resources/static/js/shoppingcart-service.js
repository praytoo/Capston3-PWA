let shoppingCartService = {

    cart: null,

    viewCart() {
        console.log("🛒 View Cart clicked");

        // Make sure app-container is visible
        document.getElementById("auth-container").style.display = "none";
        document.getElementById("app-container").style.display = "block";

        // Render cart page shell
        document.getElementById("app-container").innerHTML = `
            <div class="cart-view">
                <h2>Your Shopping Cart</h2>
                <div id="cart-items"></div>
                <div class="cart-footer">
                    <div class="cart-total">
                        <strong>Total: $<span id="cart-total">0.00</span></strong>
                    </div>
                    <div class="cart-actions">
                        <button onclick="shoppingCartService.showProducts()" class="btn-secondary">Continue Shopping</button>
                        <button onclick="shoppingCartService.checkout()" class="btn-primary">Checkout</button>
                    </div>
                </div>
            </div>
        `;

        // Load + render cart data
        this.loadCart();
    },
    showProducts() {
        console.log("🏪 Showing products");

        // Restore the products view
        document.getElementById("app-container").innerHTML = `
            <main id="product-list">
                <div id="products"></div>
            </main>
        `;

        // Load products
        loadProducts();
    },
    async checkout() {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("Please log in to checkout");
            return;
        }

        if (!this.cart || !this.cart.items || Object.keys(this.cart.items).length === 0) {
            alert("Your cart is empty!");
            return;
        }

        try {
            const response = await fetch("http://localhost:8081/orders", {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });

            if (!response.ok) {
                throw new Error("Checkout failed");
            }

            alert("✅ Order placed successfully!");

            // Clear cart and show products
            this.cart = null;
            this.showProducts();

        } catch (error) {
            console.error("Checkout error:", error);
            alert("Checkout failed: " + error.message);
        }
    },

    async loadCart() {
        console.log("📦 Loading cart...");

        const token = localStorage.getItem("token");
        if (!token) {
            alert("Please log in to view your cart");
            return;
        }

        const response = await fetch("http://localhost:8081/cart", {  // ← Added full URL
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Failed to load cart");
            return;
        }

        this.cart = await response.json();
        this.renderCart();
    },

    renderCart() {
        console.log("🎨 Rendering cart", this.cart);

        const itemsDiv = document.getElementById("cart-items");
        const totalSpan = document.getElementById("cart-total");

        if (!this.cart || !this.cart.items || Object.keys(this.cart.items).length === 0) {
            itemsDiv.innerHTML = "<p>Your cart is empty</p>";
            totalSpan.textContent = "0.00";
            return;
        }

        itemsDiv.innerHTML = "";

        let total = 0;

        Object.values(this.cart.items).forEach(item => {
            const lineTotal = item.product.price * item.quantity;
            total += lineTotal;

            const div = document.createElement("div");
            div.className = "cart-item";
            div.innerHTML = `
                <div class="cart-item-details">
                    <h3>${item.product.name}</h3>
                    <p>Price: $${item.product.price.toFixed(2)}</p>
                    <p>Quantity: ${item.quantity}</p>
                </div>
                <div class="cart-item-total">
                    <strong>$${lineTotal.toFixed(2)}</strong>
                </div>
            `;
            itemsDiv.appendChild(div);
        });

        totalSpan.textContent = total.toFixed(2);
    }
};

window.shoppingCartService = shoppingCartService;
