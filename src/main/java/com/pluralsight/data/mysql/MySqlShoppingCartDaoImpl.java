package com.pluralsight.data.mysql;

import com.pluralsight.models.Product;
import com.pluralsight.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.pluralsight.data.ShoppingCartDao;
import com.pluralsight.models.ShoppingCart;
import com.pluralsight.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MySqlShoppingCartDaoImpl extends MySqlDaoBase implements ShoppingCartDao {
    @Autowired
    public MySqlShoppingCartDaoImpl(DataSource dataSource, DataSource dataSource1) {
        super(dataSource);
        this.dataSource = dataSource1;
    }

    @Override
    public ShoppingCart getByUserId(Integer userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shopping_cart WHERE user_id = ?;");
        ) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    return new ShoppingCart(resultSet.getInt("user_id"), resultSet.getInt("product_id"), resultSet.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Map<Integer, ShoppingCartItem> getCart(Integer userId) {
        Map<Integer, ShoppingCartItem> cart = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT sc.quantity, p.product_id, p.name, p.price, p.category_id, p.description, p.subcategory, p.image_url, p.stock, p.featured FROM groceryapp.shopping_cart AS sc JOIN groceryapp.products AS p ON sc.product_id = p.product_id WHERE sc.user_id = ?;");
        ) {
            preparedStatement.setInt(1, userId);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product(resultSet.getInt("product_id"), resultSet.getString("name"), resultSet.getBigDecimal("price"), resultSet.getInt("category_id"), resultSet.getString("description"), resultSet.getString("subcategory"), resultSet.getInt("stock"), resultSet.getBoolean("featured"), resultSet.getString("image_url"));

                    ShoppingCartItem item = new ShoppingCartItem(product, resultSet.getInt("quantity"));
                    cart.put(resultSet.getInt("product_id"), item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public void addProduct(Integer userId, Integer productId, Integer quantity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity);", Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

            try (ResultSet keys = preparedStatement.getGeneratedKeys();) {

                while (keys.next()) {
                    System.out.println("Keys added: " + keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCart(Integer userId, Integer productId, ShoppingCartItem shoppingCartItem) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shopping_cart SET quantity = ?" + " WHERE user_id = ? AND product_id = ?;")) {

            preparedStatement.setInt(1, shoppingCartItem.getQuantity());
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, productId);

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /*
    @Override
    public void deleteCart(Integer userId, ShoppingCart shoppingCart) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM shopping_cart WHERE user_id = ?;")) {

            preparedStatement.setInt(1, userId);

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



     */

    @Override
    public List<ShoppingCartItem> getItemsByUserId(Integer userId) {
        List<ShoppingCartItem> cart = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT sc.user_id, sc.product_id, sc.quantity, p.product_id, p.name, p.price, p.category_id, p.description, p.subcategory, p.image_url, p.stock, p.featured FROM groceryapp.shopping_cart AS sc JOIN groceryapp.products AS p ON sc.product_id = p.product_id WHERE sc.user_id = ?;");
        ) {
            preparedStatement.setInt(1, userId);


            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product(resultSet.getInt("product_id"), resultSet.getString("name"), resultSet.getBigDecimal("price"), resultSet.getInt("category_id"), resultSet.getString("description"), resultSet.getString("subcategory"), resultSet.getInt("stock"), resultSet.getBoolean("featured"), resultSet.getString("image_url"));

                    ShoppingCartItem item = new ShoppingCartItem(product, resultSet.getInt("quantity"));
                    cart.add(item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public void clearCart(Integer userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM shopping_cart WHERE user_id = ?;")) {

            preparedStatement.setInt(1, userId);

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final DataSource dataSource;

    public void ShoppingCartDaoImpl(DataSource dataSource) {
    }

    public ShoppingCart getOrCreateCart(User user) {
        ShoppingCart cart = findByUserId(user.getId());

        if (cart == null) {
            cart = new ShoppingCart();
            cart.setUser(user);
            create(cart);
        }

        return cart;
    }

    public ShoppingCart findByUserId(int userId) {
        ShoppingCart cart = null;

        try (Connection conn = dataSource.getConnection()) {

            String cartQuery = "SELECT * FROM shopping_cart WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(cartQuery)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    cart = new ShoppingCart();
                    cart.setId(rs.getInt("id"));
                    cart.setUser(new User());
                    cart.getUser().setId(userId);
                    cart.setItems((Map<Integer, ShoppingCartItem>) getCartItems(cart.getId(), conn));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cart;
    }

    public void create(ShoppingCart cart) {
        try (Connection conn = dataSource.getConnection()) {
            String insertCart = "INSERT INTO shopping_cart (user_id) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCart, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cart.getUser().getId());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    cart.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ShoppingCart cart) {

    }

    @Override
    public void saveCartAndItems(ShoppingCart cart) throws SQLException {
        // Save the cart itself
        updateCart(cart);

        // Save each item
        for (ShoppingCartItem item : getCartItems(cart.getId(), getConnection())) {
            createOrUpdateItem(item, cart.getId());
        }
    }

    private void updateCart(ShoppingCart cart) {
    }

    private void createOrUpdateItem(ShoppingCartItem item, Integer id) {
    }

    public void addItem(int cartId, int productId, int quantity) {
        try (Connection conn = dataSource.getConnection()) {

            // Check if item already exists
            String checkItem = "SELECT id, quantity FROM shopping_cart_item WHERE cart_id = ? AND product_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkItem)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int existingId = rs.getInt("id");
                    int existingQty = rs.getInt("quantity");
                    String updateQty = "UPDATE shopping_cart_item SET quantity = ? WHERE id = ?";
                    try (PreparedStatement ups = conn.prepareStatement(updateQty)) {
                        ups.setInt(1, existingQty + quantity);
                        ups.setInt(2, existingId);
                        ups.executeUpdate();
                    }
                } else {
                    String insertItem = "INSERT INTO shopping_cart_item (cart_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement ips = conn.prepareStatement(insertItem)) {
                        ips.setInt(1, cartId);
                        ips.setInt(2, productId);
                        ips.setInt(3, quantity);
                        ips.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<ShoppingCartItem> getCartItems(int cartId, Connection conn) throws SQLException {
        List<ShoppingCartItem> items = new ArrayList<>();
        String itemQuery = "SELECT * FROM shopping_cart_item WHERE cart_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(itemQuery)) {
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setId(rs.getInt("id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                items.add(item);
            }
        }
        return items;
    }
}
