package com.s3.stock.products.store_products.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.Atribut;
import com.s3.stock.products.store_products.entitis.Category;
import com.s3.stock.products.store_products.entitis.InventoryMovement;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.ProductAttribute;
import com.s3.stock.products.store_products.entitis.ProductImage;
import com.s3.stock.products.store_products.entitis.Provider;
import com.s3.stock.products.store_products.entitis.ValueAttribut;
import com.s3.stock.products.store_products.entitis.dto.ProductAttributeDto;
import com.s3.stock.products.store_products.entitis.dto.ProductResponseDto;
import com.s3.stock.products.store_products.repositories.IAttributRepository;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;
import com.s3.stock.products.store_products.repositories.IInventoryMovementRepository;
import com.s3.stock.products.store_products.repositories.IProductAttributeRepository;
import com.s3.stock.products.store_products.repositories.IProductRepository;
import com.s3.stock.products.store_products.repositories.IProviderRepository;
import com.s3.stock.products.store_products.repositories.IValueAttributeRepository;
import com.s3.stock.products.store_products.services.interfaces.IProductServices;
import com.s3.stock.products.store_products.services.interfaces.IS3Services;
import com.s3.stock.products.store_products.util.MovementType;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServices implements IProductServices{

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IValueAttributeRepository valueAttributRepository;

    @Autowired
    private IProductAttributeRepository productAttributeRepository;

    @Autowired
    private IAttributRepository attributeRepository;

    @Autowired
    private IInventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private IProviderRepository providerRepository;

    @Autowired
    private IS3Services s3Services;


    @Transactional
    @Override
    public ProductResponseDto save(ProductResponseDto productDto) {
        try {
            // 1. Validar que la categoría exista
            Category category = categoryRepository.findByName(productDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoría"));

            // 2. Validar que existe el provider
            Provider provider = providerRepository.findByName(productDto.getProvider())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar el proveedor"));

            // 3. Generar el SKU (usando la nueva clase SkuGenerator)
            String sku = generateSku(productDto.getName(), category.getName(), productDto.getModel(), productDto.getBrand());

            // 4. Verificar si el producto ya existe (usando el SKU generado)
            productRepository.findBySku(sku).ifPresent(existingProduct -> {
                throw new IllegalArgumentException("El producto con el SKU " + sku + " ya existe");
            });

            // 5. Subir la imagen a S3 (si se proporciona)
            String imageUrl = null;
            if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
                imageUrl = s3Services.uploadFile(productDto.getImage());
            }

            // 6. Crear el producto
            Product productNew = new Product();
            productNew.setName(productDto.getName());
            productNew.setDescription(productDto.getDescription());
            productNew.setPrice(productDto.getPrice());
            productNew.setStock(productDto.getStock());
            productNew.setSku(sku); // Usar el SKU generado
            productNew.setBrand(productDto.getBrand());
            productNew.setModel(productDto.getModel());
            productNew.setCategory(category);
            productNew.setImagUrl(imageUrl); // Asignar la URL de la imagen
            productNew.getProviders().add(provider); // Asignar el proveedor
            
            // 7 relacion entre imagen y producto
            if (imageUrl != null) {
                ProductImage productImage = new ProductImage();
                productImage.setProduct(productNew);
                productImage.setImageUrl(imageUrl);
                productNew.getImages().add(productImage);
            }
            
            // 8. Guardar el producto
            productNew = productRepository.save(productNew);


            // 9. Genero la relacion con el inventario en ENTRY para el stock inicial
            InventoryMovement inventoryMovement = new InventoryMovement();
            inventoryMovement.setProduct(productNew);
            inventoryMovement.setQuantity(productNew.getStock());
            inventoryMovement.setMovement(MovementType.ENTRY);
            inventoryMovement.setNotes("Stock inicial");
            inventoryMovement.setMovementDate(java.time.LocalDateTime.now());

            // 10, Guardar el movimiento en el inventario
            inventoryMovementRepository.save(inventoryMovement);


            // 11. Guardar los atributos del producto (con optimizaciones)
            guardarAtributosDelProducto(productDto.getProductAttributes(), productNew, category);

            return convertToDto(productNew);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("El SKU ya existe. Por favor, elija un nombre de producto o categoría diferente.");
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void update(ProductResponseDto productDto, Long productId) {
        try {
            // 1. Verificar si existe el producto
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar el producto"));
            // Product product = productRepository.findBySku(productDto.getSku())
            //         .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));

            // 2. Verificar que exista la categoría
            Category category = categoryRepository.findByName(productDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoría"));

            // 3. Subir la nueva imagen a S3 (si se proporciona)
            String newImageUrl = null;
            if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
                // Subir la nueva imagen a S3
                newImageUrl = s3Services.uploadFile(productDto.getImage());

                // Eliminar la imagen anterior de S3 (si existe)
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    for (ProductImage oldImage : product.getImages()) {
                        s3Services.deleteFile(oldImage.getImageUrl()); // Eliminar la imagen anterior de S3
                    }
                    product.getImages().clear(); // Limpiar las imágenes antiguas del producto
                }

                // Crear y asociar la nueva imagen al producto
                ProductImage newImage = new ProductImage();
                newImage.setProduct(product);
                newImage.setImageUrl(newImageUrl);
                product.getImages().add(newImage);
            }

            // 4. Actualizar los campos del producto
            product.setBrand(productDto.getBrand());
            product.setCategory(category);
            product.setDescription(productDto.getDescription());
            product.setModel(productDto.getModel());
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());

            // Actualizar el stock solo si se proporciona un nuevo valor
            if (productDto.getStock() != null) {
                product.setStock(productDto.getStock());
            }
 

            // Generar nuevo SKU solo si es necesario (nombre o categoría cambiaron)
            String sku = generateSku(productDto.getName(), category.getName(), productDto.getModel(), productDto.getBrand());
            product.setSku(sku); // Usar el SKU generado

            // if (!product.getName().equals(productDto.getName()) || !product.getCategory().equals(category)) {
            //     product.setSku(generateSku(productDto.getName(), category.getName(), productDto.getModel(), productDto.getBrand()));
            // }

            // 5. Manejar los atributos del producto (con optimizaciones y creación)
            guardarAtributosDelProducto(productDto.getProductAttributes(), product, category);

            // 6. Guardar el producto actualizado
            productRepository.save(product);
        } catch (IOException e) {
            throw new RuntimeException("Error al subir o eliminar la imagen: " + e.getMessage());
        }
    }
    
    @Transactional
    @Override
    public void delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        try {
            // Eliminar la imagen anterior de S3 (si existe)
            if (product.get().getImages() != null && !product.get().getImages().isEmpty()) {
                for (ProductImage oldImage : product.get().getImages()) {
                    s3Services.deleteFile(oldImage.getImageUrl()); // Eliminar la imagen anterior de S3
                }
                product.get().getImages().clear(); // Limpiar las imágenes antiguas del producto
            }
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el producto");
        }
        
    }
    
    @Transactional(readOnly = true)
    @Override
    public ProductResponseDto findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductfindById(Long id){
       return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public void saveProduct(Product product){
        productRepository.save(product);
    }
    
    @Transactional(readOnly = true)
    @Override
    public ProductResponseDto findByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream().map(product -> {
            return convertToDto(product);
        }).toList();
    }

    @Transactional
    @Override
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDto> findByCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if(!category.isPresent()){
            throw new EntityNotFoundException("Categoria inexistente");
        }
        return productRepository.findByCategory_Name(name).stream().map( product -> {
            return convertToDto(product);
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDto> findByNameContainingAndStock(String name, Integer stock) {
        List<Product> products;
        if (name != null && stock != null) {
                products = productRepository.findByNameContainingIgnoreCaseAndStockLessThan(name.toLowerCase(), stock);
            } else if (name != null) {
                products = productRepository.findByNameContainingIgnoreCase(name.toLowerCase());
            } else if (stock != null) {
                products = productRepository.findByStockLessThan(stock);
            } else {
                products = productRepository.findAll();
            }

            return products.stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDto> findByStockGreaterThanEqual(int stock) {
        return productRepository.findByStockGreaterThanEqual(stock).stream().map(product ->convertToDto(product)).toList();
    }

    private ProductResponseDto convertToDto(Product product) {
    ProductResponseDto productDto = new ProductResponseDto();
    productDto.setId(product.getId());
    productDto.setName(product.getName());
    productDto.setDescription(product.getDescription());
    productDto.setPrice(product.getPrice());
    productDto.setCategory(product.getCategory().getName());
    productDto.setStock(product.getStock());
    productDto.setSku(product.getSku());
    productDto.setBrand(product.getBrand());
    productDto.setModel(product.getModel());

    // Cambia esta línea:
    // productDto.setProvider(product.getProviders().stream().findFirst().get().getName());
    // Por esta:
    Provider provider = product.getProviders().stream().findFirst().orElse(null);
    productDto.setProvider(provider != null ? provider.getName() : null);

    List<ProductAttributeDto> productAttributeDtos = product.getProductAttributes().stream()
        .map(attribute -> new ProductAttributeDto(attribute.getValueAttribute().getAtribut().getName(), attribute.getValueAttribute().getValue()))
        .collect(Collectors.toList());
    productDto.setProductAttributes(productAttributeDtos);

    List<String> imageUrls = product.getImages().stream()
        .map(ProductImage::getImageUrl)
        .collect(Collectors.toList());
    productDto.setImageUrls(imageUrls);

    return productDto;
    }

    private String generateSku(String productName, String categoryName, String model, String brand) {
        return productName.replaceAll("\\s+", "-").toUpperCase() + "-" + categoryName.replaceAll("\\s+", "-").toUpperCase() +"-"+ model.replace("\\s+","-").toUpperCase() + "-" + brand.replace("\\s+", "-").toUpperCase();
    }
    
    private void guardarAtributosDelProducto(List<ProductAttributeDto> productAttributesDto, Product product, Category category) {
        if (productAttributesDto != null) {
            // 1. Eliminar los atributos antiguos del producto
            productAttributeRepository.deleteAll(product.getProductAttributes());
            product.getProductAttributes().clear(); // Limpiar la lista en memoria
    
            // 2. Obtener todos los atributos y valores existentes en la base de datos (optimización)
            Map<String, Atribut> atributosMap = attributeRepository.findAll().stream()
                    .collect(Collectors.toMap(Atribut::getName, Function.identity()));
    
            Map<Pair<String, String>, ValueAttribut> valoresMap = valueAttributRepository.findAll().stream()
                    .collect(Collectors.toMap(va -> Pair.of(va.getAtribut().getName(), va.getValue()), Function.identity()));
    
            List<ProductAttribute> productAttributes = new ArrayList<>();
    
            // 3. Procesar los nuevos atributos
            for (ProductAttributeDto productAttributeDto : productAttributesDto) {
                // Obtener el atributo (si existe)
                Atribut atributo = atributosMap.get(productAttributeDto.getAttributeName());
                if (atributo == null) {
                    // Si no existe lo creamos
                    atributo = new Atribut(productAttributeDto.getAttributeName());
                    atributo.setCategorys(List.of(category));
                    atributo = attributeRepository.save(atributo);
                    atributosMap.put(atributo.getName(), atributo); // Actualizar el mapa
                }
    
                // Obtener el valor del atributo (si existe)
                ValueAttribut valor = valoresMap.get(Pair.of(productAttributeDto.getAttributeName(), productAttributeDto.getValue()));
                if (valor == null) {
                    // Si no existe lo creamos
                    valor = new ValueAttribut(productAttributeDto.getValue(), atributo);
                    valor = valueAttributRepository.save(valor);
                    valoresMap.put(Pair.of(atributo.getName(), valor.getValue()), valor); // Actualizar el mapa
                }
    
                // Crear y guardar la relación ProductAttribute
                ProductAttribute productAttribute = new ProductAttribute(product, valor, valor.getValue());
                productAttributes.add(productAttributeRepository.save(productAttribute));
            }
    
            // 4. Asignar los nuevos atributos al producto
            product.setProductAttributes(productAttributes);
        }
    }
}
