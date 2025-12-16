package com.pluralsight.controllers;

import com.pluralsight.data.ProfileDao;
import com.pluralsight.data.UserDao;
import com.pluralsight.models.Profile;
import com.pluralsight.models.User;
import com.pluralsight.models.authentication.LoginDto;
import com.pluralsight.models.authentication.LoginResponseDto;
import com.pluralsight.models.authentication.RegisterUserDto;
import com.pluralsight.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@PreAuthorize("permitAll()")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;
    private ProfileDao profileDao;

    @Autowired
    public AuthenticationController(TokenProvider tokenProvider,
                                    AuthenticationManagerBuilder authenticationManagerBuilder,
                                    UserDao userDao,
                                    ProfileDao profileDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManagerBuilder
                    .getObject()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginDto.getUsername(),
                                    loginDto.getPassword()
                            )
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.createToken(authentication, false);

            User user = userDao.getByUserName(loginDto.getUsername());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            return new ResponseEntity<>(
                    new LoginResponseDto(jwt, user),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterUserDto newUser) {
        try {
            // Check if user already exists
            if (userDao.exists(newUser.getUsername())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exists.");
            }

            // Create the user
            User user = userDao.create(
                    new User(0, newUser.getUsername(), newUser.getPassword(), newUser.getRole())
            );

            // Create the profile
            Profile profile = new Profile();
            profile.setUserId(user.getId());
            profileDao.create(profile);

            // Auto-login after registration - generate token
            Authentication authentication = authenticationManagerBuilder
                    .getObject()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    newUser.getUsername(),
                                    newUser.getPassword()
                            )
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, false);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

            return new ResponseEntity<>(
                    new LoginResponseDto(jwt, user),
                    headers,
                    HttpStatus.CREATED
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Registration failed: " + e.getMessage());
        }
    }
}