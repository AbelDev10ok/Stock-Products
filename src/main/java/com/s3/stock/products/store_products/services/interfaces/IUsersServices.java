package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.Users;
import com.s3.stock.products.store_products.entitis.dto.UserAuth;
import com.s3.stock.products.store_products.entitis.dto.UserResponse;

public interface IUsersServices {
    List<UserResponse> getAllUsers();
    Users getUsersById(Long id);
    void saveUser(UserAuth user);
    void deleteUserById(Long id);
    void updateUser(Users usersdto,Long id);
    boolean existsUser(String email);
    void disableUser(String email, boolean enabled) ;
    Users findByEmail(String email);
    Users updateUserPassword(Long id, String newPassword);
}
