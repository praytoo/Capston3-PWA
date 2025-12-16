const productGrid = document.getElementById("product-grid");

// Example static products
const products = [
    {id: 1, name: "Organic Milk", price: 5.99, imageUrl: "milk.jpg", description: "Fresh organic milk."},
    {id: 2, name: "Avocado", price: 2.49, imageUrl: "avocado.jpg", description: "Ripe avocado."},
    {id: 3, name: "Kale", price: 3.99, imageUrl: "kale.jpg", description: "Fresh kale bunch."}
];

function loadProducts() {
    productGrid.innerHTML = "";
    products.forEach(product => {
        const card = document.createElement("div");
        card.className = "product-card";
        card.innerHTML = `
            <img src="images/${product.imageUrl}" alt="${product.name}">
            <h4>${product.name}</h4>
            <p>${product.description}</p>
            <p>$${product.price}</p>
            <button onclick="addToCart(${product.id})">Add to Cart</button>
        `;
        productGrid.appendChild(card);
    });
}

function addToCart(productId) {
    console.log("Adding product", productId);
    // Here you can call your Spring Boot API to add the product
}

loadProducts();
