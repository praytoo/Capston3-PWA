package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MySqlShoppingCartDaoImpl extends MySqlDaoBase implements ShoppingCartDao {
    @Autowired
    public MySqlShoppingCartDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(Integer userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shopping_cart WHERE user_id = ?;");
        ) { preparedStatement.setInt(1, userId);

            try(ResultSet resultSet = preparedStatement.executeQuery();) {
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


            try(ResultSet resultSet = preparedStatement.executeQuery()) {
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
}
