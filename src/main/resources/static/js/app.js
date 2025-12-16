const API_BASE_URL = 'http://localhost:8081';

const logoutBtn = document.getElementById("logout-btn");
const authContainer = document.getElementById("auth-container");
const appContainer = document.getElementById("app-container");

const token = localStorage.getItem("token");

if (token) {
    authContainer.style.display = "none";
    appContainer.style.display = "block";
    logoutBtn.style.display = "inline-block";
} else {
    authContainer.style.display = "block";
    appContainer.style.display = "none";
}

// Product loading
async function loadProducts() {
    try {
        const res = await fetch('http://localhost:8080/products');
        if (!res.ok) throw new Error('Failed to fetch products');
        const products = await res.json();
        const list = document.getElementById('product-list');
        list.innerHTML = '';

        products.forEach(p => {
            const card = document.createElement('div');
            card.className = 'product-card';
            card.innerHTML = `
                <h2>${p.name}</h2>
                <p>$${p.price.toFixed(2)}</p>
                <button onclick="addToCart(${p.id})">Add to Cart</button>
            `;
            list.appendChild(card);
        });
    } catch (err) {
        console.error('Error loading products:', err);
    }
}

// Update cart count
function updateCartCount(count) {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
        cartCount.textContent = `Cart: ${count}`;
    }
}

// Fixed addToCart function for your app.js

function addToCart(productId, quantity = 1) {
    console.log('Adding to cart:', productId, 'Quantity:', quantity);

    const token = localStorage.getItem("token");

    if (!token) {
        alert("Please log in first!");
        console.error('No token found. User must login.');
        return;
    }

    console.log('Using token:', token.substring(0, 20) + '...');

    // Show loading state (optional)
    const button = event?.target;
    if (button) {
        button.disabled = true;
        button.textContent = 'Adding...';
    }

    fetch("http://localhost:8080/cart", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity
        })
    })
    .then(response => {
        console.log('Response status:', response.status);

        if (!response.ok) {
            // Log the error for debugging
            return response.text().then(text => {
                console.error('Error response:', text);
                throw new Error(`Failed to add to cart: ${response.status} ${text}`);
            });
        }

        return response.json();
    })
    .then(cart => {
        console.log("Cart updated successfully:", cart);

        // Show success message
        alert('Product added to cart!');

        // Update cart count in UI
        if (cart.items) {
            const itemCount = Object.keys(cart.items).length;
            updateCartCount(itemCount);
        }

        // Reset button state
        if (button) {
            button.disabled = false;
            button.textContent = 'Add to Cart';
        }
    })
    .catch(err => {
        console.error('Add to cart error:', err);
        alert('Failed to add to cart: ' + err.message);

        // Reset button state
        if (button) {
            button.disabled = false;
            button.textContent = 'Add to Cart';
        }
    });
}

// Alternative: If you want to use the path-based endpoint instead
// This would call POST /cart/products/1?quantity=1
function addToCartAlternative(productId, quantity = 1) {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Please log in first!");
        return;
    }

    fetch("localhost:8080/cart/products/${productId}?quantity=${quantity}", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Failed to add to cart: ${response.status}`);
        }
        return response.json();
    })
    .then(cart => {
        console.log("Cart updated:", cart);
        alert('Product added to cart!');

        if (cart.items) {
            updateCartCount(Object.keys(cart.items).length);
        }
    })
    .catch(err => {
        console.error('Error:', err);
        alert('Failed to add to cart: ' + err.message);
    });
}

// Update cart count display
function updateCartCount(count) {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
        cartCount.textContent = `Cart: ${count}`;
    }
}

// Load cart (also needs token)
async function loadCart() {
    const token = localStorage.getItem("token");

    if (!token) {
        console.log('No token, user not logged in');
        return;
    }

    try {
        const res = await fetch('http://localhost:8080/cart', {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!res.ok) {
            throw new Error('Failed to load cart');
        }

        const cart = await res.json();
        console.log('Cart loaded:', cart);

        const list = document.getElementById('cart-list');
        if (list && cart.items) {
            list.innerHTML = '';

            // cart.items is a Map/Object, so convert to array
            Object.values(cart.items).forEach(item => {
                const li = document.createElement('li');
                li.textContent = `${item.product.name} x ${item.quantity} = $${(item.product.price * item.quantity).toFixed(2)}`;
                list.appendChild(li);
            });

            // Update cart count
            updateCartCount(Object.keys(cart.items).length);

            // Show total
            const totalElement = document.getElementById('cart-total');
            if (totalElement && cart.total) {
                totalElement.textContent = `Total: $${cart.total.toFixed(2)}`;
            }
        }
    } catch (err) {
        console.error('Error loading cart:', err);
    }
}

// Remove item from cart
async function removeFromCart(productId) {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Please log in first!");
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/cart/products/${productId}`, {
            method: 'DELETE',
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!res.ok) {
            throw new Error('Failed to remove item');
        }

        const cart = await res.json();
        console.log('Item removed, cart updated:', cart);

        // Reload cart display
        loadCart();
    } catch (err) {
        console.error('Error removing item:', err);
        alert('Failed to remove item');
    }
}

// Clear entire cart
async function clearCart() {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Please log in first!");
        return;
    }

    if (!confirm('Are you sure you want to clear your cart?')) {
        return;
    }

    try {
        const res = await fetch('http://localhost:8080/cart', {
            method: 'DELETE',
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!res.ok) {
            throw new Error('Failed to clear cart');
        }

        const cart = await res.json();
        console.log('Cart cleared:', cart);

        // Reload cart display
        loadCart();
    } catch (err) {
        console.error('Error clearing cart:', err);
        alert('Failed to clear cart');
    }
}

// Load cart
async function loadCart() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.log('No token, user not logged in');
        return;
    }

    try {
        const res = await fetch('http://localhost:8080/cart', {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!res.ok) throw new Error('Failed to load cart');

        const cart = await res.json();
        const list = document.getElementById('cart-list');

        if (list && cart.items) {
            list.innerHTML = '';
            cart.items.forEach(i => {
                const li = document.createElement('li');
                li.textContent = `${i.product.name} x ${i.quantity} = $${(i.product.price * i.quantity).toFixed(2)}`;
                list.appendChild(li);
            });
        }
    } catch (err) {
        console.error('Error loading cart:', err);
    }
}

// Get DOM elements
const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");
const loginMessage = document.getElementById("login-message");
const registerMessage = document.getElementById("register-message");
const loginTab = document.getElementById("login-tab");
const registerTab = document.getElementById("register-tab");

// Tab switching
if (loginTab && registerTab) {
    loginTab.addEventListener("click", () => {
        loginForm.style.display = "flex";
        registerForm.style.display = "none";
        loginTab.classList.add("active");
        registerTab.classList.remove("active");
        // Clear messages
        loginMessage.textContent = '';
        registerMessage.textContent = '';
    });

    registerTab.addEventListener("click", () => {
        loginForm.style.display = "none";
        registerForm.style.display = "flex";
        loginTab.classList.remove("active");
        registerTab.classList.add("active");
        // Clear messages
        loginMessage.textContent = '';
        registerMessage.textContent = '';
    });
}

// LOGIN
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        loginMessage.textContent = 'Logging in...';
        loginMessage.style.color = '#666';

        const email = document.getElementById("login-email").value;
        const password = document.getElementById("login-password").value;

        try {
            const res = await fetch("http://localhost:8080/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: email,  // Backend expects 'username'
                    password: password
                })
            });

            if (!res.ok) {
                const errorData = await res.text();
                throw new Error(errorData || "Login failed!");
            }

            const data = await res.json();
            localStorage.setItem("token", data.token);

            // Hide auth, show app
            authContainer.style.display = "none";
            appContainer.style.display = "block";
            logoutBtn.style.display = "inline-block"; // ✅ ADD THIS

            loginMessage.textContent = '';
            loadProducts();

        } catch (err) {
            console.error('Login error:', err);
            loginMessage.textContent = err.message;
            loginMessage.style.color = '#d32f2f';
        }
    });
}

// REGISTER
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        registerMessage.textContent = 'Creating account...';
        registerMessage.style.color = '#666';

        const name = document.getElementById("register-name").value;
        const email = document.getElementById("register-email").value;
        const password = document.getElementById("register-password").value;

        // Validation
        if (!name || !email || !password) {
            registerMessage.textContent = 'All fields are required!';
            registerMessage.style.color = '#d32f2f';
            return;
        }

        if (password.length < 6) {
            registerMessage.textContent = 'Password must be at least 6 characters!';
            registerMessage.style.color = '#d32f2f';
            return;
        }

        try {
            console.log('Attempting registration with:', { username: email, password: '***', role: 'USER' });

            const res = await fetch("http://localhost:8080/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: email,  // Backend expects 'username', not 'email'
                    password: password,
                    role: "USER"      // Add default role
                })
            });

            console.log('Registration response status:', res.status);

            if (!res.ok) {
                // Try to get error message from response
                let errorMessage = "Registration failed!";
                try {
                    const errorData = await res.json();
                    errorMessage = errorData.message || errorData.error || errorMessage;
                } catch (e) {
                    const textError = await res.text();
                    errorMessage = textError || errorMessage;
                }
                throw new Error(errorMessage);
            }

            const data = await res.json();
            console.log('Registration successful:', data);

            // Save token
            localStorage.setItem("token", data.token);

            // Hide auth, show app
            document.getElementById("auth-container").style.display = "none";
            document.getElementById("app-container").style.display = "block";

            registerMessage.textContent = '';
            loadProducts();
        } catch (err) {
            console.error('Registration error:', err);
            registerMessage.textContent = err.message;
            registerMessage.style.color = '#d32f2f';
        }
    });
}

// Check if already logged in
window.onload = () => {
    const token = localStorage.getItem("token");
    if (token) {
        document.getElementById("auth-container").style.display = "none";
        document.getElementById("app-container").style.display = "block";
        loadProducts();
    }
};

logoutBtn.addEventListener("click", () => {
    // 1. Remove token
    localStorage.removeItem("token");

    // 2. Reset UI
    appContainer.style.display = "none";
    authContainer.style.display = "block";
    logoutBtn.style.display = "none";

    // 3. Clear cart + products UI
    document.getElementById("products").innerHTML = "";
    document.getElementById("cart-count").innerText = "Cart: 0";

    console.log("Logged out successfully");
});
