package com.s3.stock.products.store_products.entitis;


import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Atribut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    
    @ManyToMany
    @JoinTable(
        name = "category_atribut",
        joinColumns = @JoinColumn(name = "atribut_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categorys;


    public Atribut() {
    }


    public Atribut(String name) {
        this.name = name;
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


    public List<Category> getCategorys() {
        return categorys;
    }


    public void setCategorys(List<Category> categorys) {
        this.categorys = categorys;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((categorys == null) ? 0 : categorys.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Atribut other = (Atribut) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (categorys == null) {
            if (other.categorys != null)
                return false;
        } else if (!categorys.equals(other.categorys))
            return false;
        return true;
    }

    
    
    
}
