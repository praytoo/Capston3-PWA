package com.pluralsight.data;

import com.pluralsight.models.RegisterRequest;
import org.springframework.stereotype.Component;
import com.pluralsight.models.User;

import java.util.List;

@Component
public interface UserDao {

    List<User> getAll();

    User getUserById(int userId);

    User getByUserName(String username);

    int getIdByUsername(String username);

    User create(User user);

    boolean exists(String username);

    RegisterRequest registerAndReturnToken(RegisterRequest request);

    void save(User user);


    User findByUsername(String username);
}
