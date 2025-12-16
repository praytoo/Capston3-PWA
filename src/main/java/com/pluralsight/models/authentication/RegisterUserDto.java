package com.pluralsight.models.authentication;

public class RegisterUserDto {
    private String username;
    private String password;
    private String role;

    public RegisterUserDto() {
    }

    public RegisterUserDto(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        // Default to USER if role is null or empty
        this.role = (role == null || role.isEmpty()) ? "USER" : role;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}