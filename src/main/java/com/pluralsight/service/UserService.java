package com.pluralsight.service;

import com.pluralsight.models.User;
import com.pluralsight.models.RegisterRequest;
import com.pluralsight.models.authentication.Authority;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.pluralsight.data.UserDao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    private final String jwtSecret = "YourSuperSecretKey"; // Use environment variable in prod
    private final long jwtExpirationMs = 86400000; // 1 day

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registerAndReturnToken(RegisterRequest request) {

        // 1. Hash password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Create user
        User user = new User();
        user.setUsername(request.getName());
        user.setPassword(encodedPassword);

        // 3. Set role directly (matches DB)
        user.addRole("ROLE_USER"); // internally stored, but DB only gets role

        // 4. Save user
        userDao.save(user);

        // 5. Generate JWT
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole()) // SINGLE role
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

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
