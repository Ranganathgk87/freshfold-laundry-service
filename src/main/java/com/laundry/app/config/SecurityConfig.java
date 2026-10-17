package com.laundry.app.config;

import com.laundry.app.repo.AccountRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean UserDetailsService userDetailsService(AccountRepository accounts){
        return email -> accounts.findByEmail(email.toLowerCase()).map(a ->
            User.withUsername(a.getEmail()).password(a.getPassword()).roles(a.getRole()).build())
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a.requestMatchers("/", "/register", "/css/**", "/js/**", "/robots.txt", "/sitemap.xml", "/health", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/customer/**").hasRole("CUSTOMER").anyRequest().authenticated())
            .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/home", true).permitAll())
            .logout(l -> l.logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}

