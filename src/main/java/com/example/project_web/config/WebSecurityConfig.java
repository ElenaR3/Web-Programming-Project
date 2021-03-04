package com.example.project_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

 //   private final UserDetailsService userDetails;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;


    public WebSecurityConfig(PasswordEncoder passwordEncoder, CustomAuthenticationProvider customAuthenticationProvider) {
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

    /*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/books/login","/images/**","/resources/**","/static/**", "/css/**", "/js/**");
    } */

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/login","/books/sign-up","/books/homepage",
                        "/assets/**","/api/**", "/login/oauth2/code/google","/images/**","/resources/**","/static/**",
                        "/css/**", "/js/**").permitAll()
                .antMatchers("/myBooks/**", "/books/**").hasAnyRole("USER", "ADMIN")
                .and()
                .formLogin()
                .loginPage("/books/login").permitAll()
                .failureUrl("/login?error=BadCredentials")
                .defaultSuccessUrl("/myBooks/sellBooks", true)
                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/books/login")
                .and()
                .exceptionHandling().accessDeniedPage("/shop/accessDenied")
                .and().oauth2Login() // enable OAuth2
                .loginPage("/login").defaultSuccessUrl("/myBooks/sellBooksAuth");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(customAuthenticationProvider);

    }

}
