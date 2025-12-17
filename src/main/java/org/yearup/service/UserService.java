package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.UserDao;
import org.yearup.models.User;

import java.util.List;

@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    //methods that the user dao implements

    public List<User> getAll(){
        return userDao.getAll();
    }

    public User getUserById(int userId){
        return userDao.getUserById(userId);
    }

    public User getByUserName(String username){
        return userDao.getByUserName(username);
    }

    public int getIdByUsername(String username){
        return userDao.getIdByUsername(username);
    }

    public User create(User user){
        return userDao.create(user);
    }

    public boolean exists(String username){
        return userDao.exists(username);
    }
}
