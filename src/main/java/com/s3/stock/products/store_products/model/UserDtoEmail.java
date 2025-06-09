package com.s3.stock.products.store_products.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDtoEmail {
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email no es válido")
    private String email;

        public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
