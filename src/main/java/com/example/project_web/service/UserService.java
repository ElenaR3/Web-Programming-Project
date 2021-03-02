package com.example.project_web.service;

import com.example.project_web.model.Role;
import com.example.project_web.model.User;
import com.example.project_web.model.exceptions.InvalidArgumentsExceptions;
import com.example.project_web.model.exceptions.InvalidUserCredentials;
import com.example.project_web.model.exceptions.UserNotFoundException;
import com.example.project_web.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

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

    public Optional<User> findByUsername(String s) {
        return userRepository.findByUsername(s);
    }

    public static String alphaNumericString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public String randomPassGenerator() {
        return passwordEncoder.encode(alphaNumericString(10));
    }


}
