package com.example.project_web.web.controllers;

import com.example.project_web.model.Role;
import com.example.project_web.model.User;
import com.example.project_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Controller
@RequestMapping("books")
public class BookStoreController {

    private final UserService userService;

    public BookStoreController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/welcome")
    public String getPersonalHomepage(Model model){
        model.addAttribute("bodyContentUser", "landing_page");
        return "master-user-template";
    }

    @GetMapping("/homepage")
    public String getHomepage(Model model){
        model.addAttribute("bodyContent", "landing_page");
        return "master-template";
    }

    @GetMapping("/sign-up")
    public String userSignUp(Model model) {
        model.addAttribute("bodyContent", "sign_up_form");
        return "master-template";
    }

    @GetMapping("/login")
    public String login (Model model) {
        model.addAttribute("bodyContent", "login");
        return "master-template";
    }

    @PostMapping("/sign-up")
    public String create(@RequestParam String name, @RequestParam String surname,
                         @RequestParam String username, @RequestParam String password,
                         @RequestParam Role role, Model model) {
        model.addAttribute("bodyContent", "login");
        this.userService.create(name, surname, username,password,role);
        return "master-template";

    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        model.addAttribute("bodyContent", "landing_page");
        this.userService.login(username, password);
        return "master-template";
    }

}