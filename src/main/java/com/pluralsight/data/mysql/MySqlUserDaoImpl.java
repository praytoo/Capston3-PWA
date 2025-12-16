package com.pluralsight.data.mysql;

import com.pluralsight.models.RegisterRequest;
import com.pluralsight.models.authentication.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import com.pluralsight.data.UserDao;
import com.pluralsight.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MySqlUserDaoImpl extends MySqlDaoBase implements UserDao
{
    @Autowired
    public MySqlUserDaoImpl(DataSource dataSource)
    {
        super(dataSource);
    }


    @Override
    public User create(User newUser)
    {
        String sql = "INSERT INTO users (username, hashed_password, role) VALUES (?, ?, ?)";
        String hashedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());

            ps.executeUpdate();

            User user = getByUserName(newUser.getUsername());
            user.setPassword("");

            return user;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll()
    {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                User user = mapRow(row);
                users.add(user);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public User getUserById(int id)
    {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet row = statement.executeQuery();

            if(row.next())
            {
                User user = mapRow(row);
                return user;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User getByUserName(String username)
    {
        String sql = "SELECT * " +
                " FROM users " +
                " WHERE username = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet row = statement.executeQuery();
            if(row.next())
            {

                User user = mapRow(row);
                return user;
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }

        return null;
    }

    @Override
    public int getIdByUsername(String username)
    {
        User user = getByUserName(username);

        if(user != null)
        {
            return user.getId();
        }

        return -1;
    }

    @Override
    public boolean exists(String username)
    {
        User user = getByUserName(username);
        return user != null;
    }

    @Override
    public RegisterRequest registerAndReturnToken(RegisterRequest request) {
        return null;
    }

    private User mapRow(ResultSet row) throws SQLException
    {
        int userId = row.getInt("user_id");
        String username = row.getString("username");
        String hashedPassword = row.getString("hashed_password");
        String role = row.getString("role");

        return new User(userId, username,hashedPassword, role);
    }

    public void save(User user) {

        String sql = """
        INSERT INTO users (username, hashed_password, role)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            stmt.setString(3, user.getRole());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }


}
