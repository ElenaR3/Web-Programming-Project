package com.example.project_web.service;

import com.example.project_web.model.Role;
import com.example.project_web.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserServiceInterface extends UserDetailsService {

    User create(String name, String surname, String username, String password, Role role);
    User login(String username, String password);
}
