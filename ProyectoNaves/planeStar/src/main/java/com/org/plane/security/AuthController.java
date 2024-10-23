package com.org.plane.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SecurityConfig securityConfig;

    @PostMapping("/authenticate")
    @Operation(summary = "Autenticación de usuario", description = "Autentica a un usuario y genera un token JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token JWT generado exitosamente."),
        @ApiResponse(responseCode = "401", description = "Nombre de usuario o contraseña incorrectos.")
    })
    public ResponseEntity<String> createAuthenticationToken(
            @Parameter(description = "Credenciales del usuario para autenticación", required = true) 
            @RequestBody AuthRequest authRequest) throws Exception {
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = securityConfig.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(jwt);
    }
}