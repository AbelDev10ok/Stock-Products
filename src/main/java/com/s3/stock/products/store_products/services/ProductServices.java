package com.s3.stock.products.store_products.services;

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
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.ProductAttribute;
import com.s3.stock.products.store_products.entitis.ValueAttribut;
import com.s3.stock.products.store_products.entitis.dto.ProductAttributeDto;
import com.s3.stock.products.store_products.entitis.dto.ProductDto;
import com.s3.stock.products.store_products.repositories.IAttributRepository;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;
import com.s3.stock.products.store_products.repositories.IProductAttributeRepository;
import com.s3.stock.products.store_products.repositories.IProductRepository;
import com.s3.stock.products.store_products.repositories.IValueAttributeRepository;

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

    private void guardarAtributosDelProducto(List<ProductAttributeDto> productAttributesDto, Product product, Category category) {
        if (productAttributesDto != null) {

            // Obtener todos los atributos y valores existentes en la base de datos (optimización)
            Map<String, Atribut> atributosMap = attributeRepository.findAll().stream()
                    .collect(Collectors.toMap(Atribut::getName, Function.identity()));

            Map<Pair<String, String>, ValueAttribut> valoresMap = valueAttributRepository.findAll().stream()
                    .collect(Collectors.toMap(va -> Pair.of(va.getAtribut().getName(), va.getValue()), Function.identity()));

            List<ProductAttribute> productAttributes = new ArrayList<>();

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

            product.setProductAttributes(productAttributes);
        }
    }

    @Transactional
    @Override
    public void save(ProductDto productDto) {

        try {
            // 1. Validar que la categoría exista
            Category category = categoryRepository.findByName(productDto.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoría"));

            // 2. Generar el SKU (usando la nueva clase SkuGenerator)
            String sku = generateSku(productDto.getName(), category.getName(), productDto.getModel(), productDto.getBrand());

            // 3. Verificar si el producto ya existe (usando el SKU generado)
            productRepository.findBySku(sku).ifPresent(existingProduct -> {
                throw new IllegalArgumentException("El producto con el SKU " + sku + " ya existe");
            });

            // 4. Crear el producto
            Product productNew = new Product();
            productNew.setName(productDto.getName());
            productNew.setDescription(productDto.getDescription());
            productNew.setPrice(productDto.getPrice());
            productNew.setStock(productDto.getStock());
            productNew.setSku(sku); // Usar el SKU generado
            productNew.setBrand(productDto.getBrand());
            productNew.setModel(productDto.getModel());
            productNew.setCategory(category);

            // 5. Guardar el producto
            productNew = productRepository.save(productNew);

            // 6. Guardar los atributos del producto (con optimizaciones)
            guardarAtributosDelProducto(productDto.getProductAttributes(), productNew, category);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("El SKU ya existe. Por favor, elija un nombre de producto o categoría diferente.");
        }
    }

    @Transactional
    @Override
    public void update(ProductDto productDto) {
        // 1. Verificar si existe el producto
        Product product = productRepository.findBySku(productDto.getSku())
                .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));

        // 2. Verificar que exista la categoría
        Category category = categoryRepository.findByName(productDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoría"));

        product.setBrand(productDto.getBrand());
        product.setCategory(category);
        product.setDescription(productDto.getDescription());
        product.setModel(productDto.getModel());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());

        // Generar nuevo SKU solo si es necesario (nombre o categoría cambiaron)
        if (!product.getName().equals(productDto.getName()) || !product.getCategory().equals(category)) {
            product.setSku(generateSku(productDto.getName(), category.getName(), productDto.getModel(), productDto.getBrand()));
        }

        product.setStock(productDto.getStock());

        // 3. Manejar los atributos del producto (con optimizaciones y creación)
        guardarAtributosDelProducto(productDto.getProductAttributes(), product, category);

        productRepository.save(product);
    }



    // @Transactional
    // @Override
    // public void save(ProductDto productDto) {

    //     try{

    //         // 1 validar que la categoria exista y esté gestionada
    //         Category category = categoryRepository.findByName(productDto.getCategory())
    //             .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoria"));

    //         // 2 generar el SKU
    //         String sku = generateSku(productDto.getName(), category.getName(),productDto.getModel(),productDto.getBrand());

    //         // 3 verificar si el producto ya existe
    //         productRepository.findBySku(sku).ifPresent(existingProduct -> {
    //             throw new IllegalArgumentException("El producto con el SKU " + sku + " ya existe");
    //         });

    //         // 4 crear el producto
    //         Product productNew = new Product();
    //         productNew.setName(productDto.getName());
    //         productNew.setDescription(productDto.getDescription());
    //         productNew.setPrice(productDto.getPrice());
    //         productNew.setStock(productDto.getStock());
    //         productNew.setSku(generateSku(productDto.getName(), category.getName(),productDto.getModel(),productDto.getBrand()));
    //         productNew.setBrand(productDto.getBrand());
    //         productNew.setModel(productDto.getModel());
    //         productNew.setCategory(category);

    //         // 5 guardar el producto
    //         productNew = productRepository.save(productNew);
    //         List<Category> categories = new ArrayList<>();
    //         categories.add(productNew.getCategory());

    //         // 6 guardar los atributos del producto
    //         if (productDto.getProductAttributes() != null) {

    //             List<ProductAttribute> productAttributes = new ArrayList<>();
                
    //             for (ProductAttributeDto productAttribute : productDto.getProductAttributes()) {
                    

    //                 // si no tenemos el atributo lo creamos
    //                 Optional<Atribut> atributeOptional = attributeRepository.findByName(productAttribute.getAttributeName());
    //                 Atribut newAtribut;
    //                 if(atributeOptional.isPresent()){
    //                     newAtribut = atributeOptional.get();
    //                 }else{

    //                     newAtribut = new Atribut();
    //                     newAtribut.setName(productAttribute.getAttributeName());
    //                     // Asocia el atributo a la categoría del producto
    //                     newAtribut.setCategorys(List.of(category)); 
    //                     attributeRepository.save(newAtribut); 
    //                 }
                    
    //                 ValueAttribut valueAttribute = valueAttributRepository.findByValueAndAtributName(productAttribute.getValue(), productAttribute.getAttributeName())
    //                 .orElseGet(() -> {  

    //                     // si no tenemos el valor del atributo lo creamos
    //                     ValueAttribut newValueAttribute = new ValueAttribut();
    //                     newValueAttribute.setValue(productAttribute.getValue());
    //                     newValueAttribute.setAtribut(newAtribut);

    //                     // newValueAttribute.setAtribut(attributeRepository.findByName(productAttribute.getAttributeName())
    //                     //     .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar el atributo")));
    //                     return valueAttributRepository.save(newValueAttribute);
    //                 });

    //                 // 7 crea la relación entre el producto y el atributo
    //                 ProductAttribute productAttributeNew = new ProductAttribute();
    //                 productAttributeNew.setValueAttribute(valueAttribute);
    //                 productAttributeNew.setValue(valueAttribute.getValue());
    //                 productAttributeNew.setProduct(productNew);

    //                 // 8 guardar la relación
    //                 productAttributes.add(productAttributeRepository.save(productAttributeNew));
    //             }

    //             productNew.setProductAttributes(productAttributes);
    //         }

    //     } catch (DataIntegrityViolationException e) {
    //         // cuando se viola la restricción de integridad
    //         throw new IllegalArgumentException("El SKU ya existe. Por favor, elija un nombre de producto o categoría diferente.");
    //     }
    // }

    // @Transactional
    // @Override
    // public void update(ProductDto productDto){
    //     // 1 verificamos si existe el producto
    //     Product product = productRepository.findBySku(productDto.getSku()).orElseThrow( ()-> new EntityNotFoundException("Producto no existe"));
        
    //     // 2 verificamos que exista la categoria
    //     Category category = categoryRepository.findByName(productDto.getCategory())
    //     .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar la categoria"));

    //     product.setBrand(productDto.getBrand());
    //     product.setCategory(category);
    //     product.setDescription(productDto.getDescription());
    //     product.setModel(productDto.getModel());
    //     product.setName(productDto.getName());
    //     product.setPrice(productDto.getPrice());
    //     product.setSku(generateSku(productDto.getName(), category.getName(),productDto.getModel(),productDto.getBrand()));
    //     product.setStock(productDto.getStock());
        
    //     List<ProductAttribute> productAttributes = new ArrayList<>();

        
        
    //     for (ProductAttributeDto productAttribute : productDto.getProductAttributes()) {
            
            
    //         ValueAttribut valueAttribute = valueAttributRepository.findByValueAndAtributName(productAttribute.getValue(), productAttribute.getAttributeName())
    //         .orElseGet(() -> {
                
    //             // si no tenemos el valor del atributo lo creamos
    //             ValueAttribut newValueAttribute = new ValueAttribut();
    //             newValueAttribute.setValue(productAttribute.getValue());
                
    //             // si no existe el nombre del atributo lanzamos excepcion
    //             newValueAttribute.setAtribut(attributeRepository.findByName(productAttribute.getAttributeName())
    //             .orElseThrow(() -> new EntityNotFoundException("No se pudo encontrar el atributo")));
    //             return valueAttributRepository.save(newValueAttribute);
    //         });
            
    //         // elimino la relacion que ya tenia entre los productos y los atributos, ya que se crearan otra ves.
    //         productAttributeRepository.deleteByProductIdAndValueAttributeAtributId(product.getId(),valueAttribute.getAtribut().getId());

    //         ProductAttribute productAttributeNew = new ProductAttribute();
        
    //         productAttributeNew.setValueAttribute(valueAttribute);
    //         productAttributeNew.setValue(valueAttribute.getValue());
    //         productAttributeNew.setProduct(product);


    //         productAttributes.add(productAttributeRepository.save(productAttributeNew));

    //     }
    //     product.setProductAttributes(productAttributes);

    //     productRepository.save(product);

    // }
    
    @Transactional
    @Override
    public void delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el producto");
        }
        
    }
    
    @Transactional(readOnly = true)
    @Override
    public ProductDto findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public Product getProductfindById(Long id){
       return productRepository.findById(id).get();
    }

    @Transactional
    public void saveProduct(Product product){
        productRepository.save(product);
    }
    @Transactional(readOnly = true)
    @Override
    public ProductDto findByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> findAll() {
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
    public List<ProductDto> findByCategory(String name) {
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
    public List<ProductDto> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name).stream().map(product -> {
            return convertToDto(product);
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> findByStockGreaterThanEqual(int stock) {
        return productRepository.findByStockGreaterThanEqual(stock).stream().map(product ->convertToDto(product)).toList();
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setCategory(product.getCategory().getName());
        productDto.setStock(product.getStock());
        productDto.setSku(product.getSku());
        productDto.setBrand(product.getBrand());
        productDto.setModel(product.getModel());

        List<ProductAttributeDto> productAttributeDtos = product.getProductAttributes().stream()
            .map(attribute -> new ProductAttributeDto(attribute.getValueAttribute().getAtribut().getName(),attribute.getValueAttribute().getValue()))
            .collect(Collectors.toList());
        productDto.setProductAttributes(productAttributeDtos);

        return productDto;
    }


    private String generateSku(String productName, String categoryName, String model, String brand) {
        return productName.replaceAll("\\s+", "-").toUpperCase() + "-" + categoryName.replaceAll("\\s+", "-").toUpperCase() +"-"+ model.replace("\\s+","-").toUpperCase() + "-" + brand.replace("\\s+", "-").toUpperCase();
    }
}
