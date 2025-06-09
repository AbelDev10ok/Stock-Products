package com.s3.stock.products.store_products.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.config.security.JwtUtil;
import com.s3.stock.products.store_products.entitis.Users;
import com.s3.stock.products.store_products.entitis.dto.UserAuth;
import com.s3.stock.products.store_products.repositories.IUsersRepository;
import com.s3.stock.products.store_products.services.interfaces.IUsersServices;
import com.s3.stock.products.store_products.util.ValidationDto;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@RestController
@RequestMapping("${api.base.path}/auth")
public class AuthController {

    @Autowired
    private IUsersServices  userServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsersRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ValidationDto validationDto;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserAuth entity, BindingResult result) {   
        if(result.hasErrors()) {
            return validationDto.validation(result);
        }

        try {
            userServices.saveUser(entity);     
            return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserAuth entity, BindingResult result) {
        if(result.hasErrors()){
            return validationDto.validation(result);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(entity.getEmail(), entity.getPassword())
            );

            String name = ((User) authentication.getPrincipal()).getUsername();

            Users user = userRepository.findByEmail(name)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("error Usuario deshabilitado");
            }
    

            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(Map.of("id",user.getId(),"name",user.getEmail(),"token", token));
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("error Credenciales incorrectas");
        }
        catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error Error interno del servidor");
        }
    }

}
