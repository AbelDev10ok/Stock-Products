package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.Users;
import com.s3.stock.products.store_products.repositories.IUsersRepository;

// cada vez que hagamos login spring config utilizara esta clase ya que implementas UserDetailsServices


// ¿En qué parte o momento de la app se ejecuta?
// Este servicio se ejecuta durante el proceso de autenticación de Spring Security.
// Cuando un usuario intenta iniciar sesión, el AuthenticationManager de Spring Security utiliza el UserDetailsService configurado 
// para cargar los detalles del usuario.
// Específicamente, el AuthenticationManager llama al método loadUserByUsername(String username) para obtener la información del usuario
// basándose en el nombre de usuario proporcionado en la solicitud de inicio de sesión.
// En tu filtro JwtAuthentication, cuando haces authenticationManager.authenticate(authToken);, este AuthenticationManager es quien llama al UserDetailsService.

// ¿Para qué sirve?
// Cargar detalles del usuario: Su función principal es proporcionar a Spring Security los detalles del usuario necesarios para la autenticación.
// Autorización: Los roles (autoridades) cargados por este servicio se utilizan para determinar si un usuario tiene permiso para acceder a ciertos recursos de la aplicación.
// Integración con Spring Security: Sirve como un puente entre tu modelo de datos de usuarios y el sistema de autenticación de Spring Security.
// Abstracción: Separa la lógica de carga de usuarios de la lógica de autenticación, lo que hace que tu código sea más modular y mantenible.

@Service
public class UserDetailsServices implements UserDetailsService {

    @Autowired
    private IUsersRepository userRepository;


    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByEmail(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        Users userDb = user.orElseThrow();
        List<GrantedAuthority> roles = userDb.getRoles().stream()
        .map(role -> (GrantedAuthority) role::getName)
        .toList();

        // User de springSecurity
        return User.builder()
        .username(userDb.getEmail())
        .password(userDb.getPassword())
        .disabled(!userDb.isEnabled())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .authorities(roles)
        .build();
    }

}
