package com.s3.stock.products.store_products.entitis.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

// import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductResponseDto {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;
    @NotNull(message = "Category is required")
    private String category;
    @NotNull(message = "Stock is required")
    @Min(value = 1, message = "Stock must be greater than 0")
    private Integer stock;

    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Provider is required")
    private String provider;


    private List<ProductAttributeDto> productAttributes = new ArrayList<>();

    private List<String> imageUrls; // Nuevo campo para las URLs de las imágenes

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Solo para escritura (deserialización)
    private MultipartFile image;

    
    
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // Esta anotación se utiliza para controlar el acceso de lectura y escritura del campo en el JSON. 
    // JsonProperty.Access.READ_ONLY indica que el campo solo debe ser incluido en la salida JSON (serialización) y no debe ser considerado durante la entrada JSON (deserialización).
    
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // Esta anotación se utiliza para indicar que el campo solo debe incluirse en la salida JSON 
    // si su valor no es null. Si el valor del campo es null, no se incluirá en la salida JSON.
    private String sku;


    public ProductResponseDto() {
    }


    


    public ProductResponseDto(@NotBlank(message = "Name is required") String name,
            @NotBlank(message = "Description is required") String description,
            @NotNull(message = "Price is required") @Min(value = 1, message = "Price must be greater than 0") Double price,
            @NotNull(message = "Category is required") String category,
            @NotNull(message = "Stock is required") @Min(value = 1, message = "Stock must be greater than 0") int stock,
            @NotBlank(message = "Brand is required") String brand,
            @NotBlank(message = "Model is required") String model,
            @NotBlank(message = "Provider is required") String provider, List<ProductAttributeDto> productAttributes,
            List<String> imageUrls, MultipartFile image, String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.brand = brand;
        this.model = model;
        this.provider = provider;
        this.productAttributes = productAttributes;
        this.imageUrls = imageUrls;
        this.image = image;
        this.sku = sku;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductAttributeDto> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(List<ProductAttributeDto> productAttributes) {
        this.productAttributes = productAttributes;
    }


    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile imageUrl) {
		this.image = imageUrl;
	}



    public String getProvider() {
        return provider;
    }



    public void setProvider(String provider) {
        this.provider = provider;
    }

        
}
