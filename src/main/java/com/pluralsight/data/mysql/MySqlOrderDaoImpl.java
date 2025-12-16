package com.pluralsight.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.pluralsight.data.OrderDao;
import com.pluralsight.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class MySqlOrderDaoImpl extends MySqlDaoBase implements OrderDao {
    @Autowired
    public MySqlOrderDaoImpl(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public Integer createOrder(Integer userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO orders (user_id, date, address, city, state, zip) SELECT user_id, NOW(), address, city, state, zip FROM profiles WHERE user_id = ?;", Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setInt(1, userId);

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

        return userId;
    }

    @Override
    public void addOrderLineItem(Integer orderId, ShoppingCartItem item) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO order_line_items (order_id, product_id, quantity, sales_price) SELECT o.order_id, sc.product_id, sc.quantity, p.price FROM groceryapp.shopping_cart AS sc JOIN groceryapp.products AS p ON sc.product_id = p.product_id JOIN groceryapp.orders AS o ON sc.user_id = o.user_id WHERE o.order_id = ?;", Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setInt(1, orderId);

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
}
