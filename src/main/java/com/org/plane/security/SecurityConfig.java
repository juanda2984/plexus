package com.org.plane.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Value("${app.user.username}")
    private String username;

    @Value("${app.user.password}")
    private String password;

    @Value("${app.secret.key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/authenticate")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(NoOpPasswordEncoder.getInstance());
        return authenticationManagerBuilder.build();
    }

    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }
}