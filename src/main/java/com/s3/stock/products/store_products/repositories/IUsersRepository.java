package com.s3.stock.products.store_products.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.Users;

@Repository
public interface IUsersRepository extends JpaRepository<Users,Long> {
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

}
