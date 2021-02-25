package com.example.project_web.service;

import com.example.project_web.model.Role;
import com.example.project_web.model.User;
import com.example.project_web.model.exceptions.InvalidArgumentsExceptions;
import com.example.project_web.model.exceptions.InvalidUserCredentials;
import com.example.project_web.model.exceptions.InvalidUsernameException;
import com.example.project_web.model.exceptions.UserNotFoundException;
import com.example.project_web.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(String name, String surname, String username, String password, Role role) {
        String encodedPassword = this.passwordEncoder.encode(password);
            User user = new User(name, surname, username, encodedPassword, role);
            return this.userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {

            if (username==null || username.isEmpty() || password==null || password.isEmpty()) {
                throw new InvalidArgumentsExceptions();
            }
            return userRepository.findByUsernameAndPassword(username,password).orElseThrow(InvalidUserCredentials::new);

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseThrow(()-> new UserNotFoundException(s));
    }


}
