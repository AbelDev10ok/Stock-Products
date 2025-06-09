package com.s3.stock.products.store_products.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.Role;
import com.s3.stock.products.store_products.entitis.Users;
import com.s3.stock.products.store_products.entitis.dto.UserAuth;
import com.s3.stock.products.store_products.entitis.dto.UserResponse;
import com.s3.stock.products.store_products.repositories.IRoleRepository;
import com.s3.stock.products.store_products.repositories.IUsersRepository;
import com.s3.stock.products.store_products.services.interfaces.IUsersServices;



@Service
public class UsersServices implements IUsersServices{

    @Autowired
    private IUsersRepository userRepository;

    @Autowired 
    private IRoleRepository roleRepository;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }

    @Transactional(readOnly = true)
    @Override
    public Users getUsersById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'getUsersById'");
    }

    @Transactional
    @Override
    public void saveUser(UserAuth user) {
        boolean exists = userRepository.existsByEmail(user.getEmail());
        if(exists){
            throw new RuntimeException("email exists");
        }
        Optional<Role> roleUser = roleRepository.findByName("ROLE_ADMIN");   
        List<Role> roles = new ArrayList<>();
        roleUser.ifPresent(roles::add);
        // if(user.isAdmin()){
        //     System.out.println(user.isAdmin());
        //     Optional<Role> roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        //     roleAdmin.ifPresent(roles::add);
        // }
        // user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users userDb = modelMapper.map(user, Users.class);
        userDb.setRoles(roles);
        userRepository.save(userDb);
    }

    @Override
    public void deleteUserById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserById'");
    }

    @Override
    public void updateUser(Users usersdto, Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public boolean existsUser(String email) {
        throw new UnsupportedOperationException("Unimplemented method 'existsUser'");
    }

    @Override
    public void disableUser(String email, boolean enabled) {
        throw new UnsupportedOperationException("Unimplemented method 'disableUser'");
    }

    @Override
    public Users findByEmail(String email) {
        throw new UnsupportedOperationException("Unimplemented method 'findByEmail'");
    }

    @Override
    public Users updateUserPassword(Long id, String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUserPassword'");
    }

}
