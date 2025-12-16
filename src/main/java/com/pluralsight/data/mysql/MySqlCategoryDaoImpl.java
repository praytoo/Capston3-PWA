package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySqlCategoryDaoImpl extends MySqlDaoBase implements CategoryDao
{
    @Autowired
    public MySqlCategoryDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories(String name) {
        StringBuilder query = new StringBuilder("SELECT * FROM categories WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if(name != null){
            query.append(" AND name = ?");
            params.add(name);
        }
        List<Category> categories = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
        ) {
            for (int i = 0; i < params.size(); i++){
                preparedStatement.setObject(i + 1, params.get(i));
            }

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String name2 = resultSet.getString("name");
                    int categoryId = resultSet.getInt("category_id");
                    String description = resultSet.getString("description");

                    categories.add(new Category(categoryId, name2, description));
                }
            }

        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM categories WHERE category_id = ?;");
        ) { preparedStatement.setInt(1, categoryId);

            try(ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    return new Category(resultSet.getInt("category_id"), resultSet.getString("name"), resultSet.getString("description"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        // create a new category
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO categories (Name, Description) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

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

        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE categories SET Name = ?, Description = ?" + "WHERE category_id = ?;")) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getCategoryId());

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        // delete category
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM categories WHERE category_id = ?;")) {

            preparedStatement.setInt(1, categoryId);

            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows updated: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
