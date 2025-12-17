package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.User;

import java.util.List;

@Component
public interface UserDao {
    //methods to be overridden in UserDaoImpl

    List<User> getAll();

    User getUserById(int userId);

    User getByUserName(String username);

    int getIdByUsername(String username);

    User create(User user);

    boolean exists(String username);
}
