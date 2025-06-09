package com.s3.stock.products.store_products.entitis.dto;

import com.s3.stock.products.store_products.entitis.Role;

public class UserResponse {
    private Long id;
    private String name;
    private Role role;
    private boolean isEnabled;


    public UserResponse() {
    }   

    public UserResponse(Long id, String name, Role role, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.isEnabled = isEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    
}
