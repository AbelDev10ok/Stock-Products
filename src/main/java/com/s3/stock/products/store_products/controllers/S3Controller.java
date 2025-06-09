package com.s3.stock.products.store_products.controllers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.ProductImage;
import com.s3.stock.products.store_products.services.interfaces.IProductServices;
import com.s3.stock.products.store_products.services.interfaces.IS3Services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("${api.base.path}/s3")
public class S3Controller {
    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private IS3Services s3Services;

    @Autowired
    private IProductServices productServices;

    @PostMapping("/create")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName){
        return ResponseEntity.ok(this.s3Services.createBucket(bucketName));
    }

    @GetMapping("/check/{bucketName}")
    public ResponseEntity<String> checkBucket(@PathVariable String bucketName){    
        System.out.println("sadasdasd"+bucketName);        
        return ResponseEntity.ok(this.s3Services.checkIfBucketExist(bucketName));

    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listBucket(){
        return ResponseEntity.ok(this.s3Services.getAllBuckets());
    }

    // podemos cargar jpa, png, pdf , html etc.
    // @PostMapping("/upload")
    // public ResponseEntity<String> uploadFile(@RequestParam String bucketName, @RequestParam String key, @RequestPart MultipartFile file) throws IOException{
    
    //     try {
    //         Path staticDir = Paths.get(destinationFolder);
    //         // creamos la carpeta si no existe
    //         if(!Files.exists(staticDir)){
    //             Files.createDirectories(staticDir);
    //         }
    //         // le agregamos a la ruta el nombre del archivo
    //         Path filePath = staticDir.resolve(file.getOriginalFilename());
    //         Path finalPath = Files.write(filePath, file.getBytes());
            
    //         Boolean result = this.s3Services.uploadFile(bucketName, key, finalPath);
    //         if(result){
    //             // si se cargo nesecito borrar el archivo
    //             Files.delete(finalPath);
    //             return ResponseEntity.ok("Archivo cargado correctamente");
    //         }
    //         return ResponseEntity.internalServerError().body("Error al cargar el archivo al bucket");
    //     }catch (IOException e) {
    //         throw new IOException("Error al procesar el archivo");
    //     }
    // }


    @PostMapping("/{productId}/images")
    public String uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productServices.getProductfindById(productId);
        String imageUrl = s3Services.uploadFile(file);

        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(imageUrl);
        product.addImage(productImage);
        product.setImagUrl(imageUrl);

        productServices.saveProduct(product);
        return "Imagen subida y asociada al producto correctamente.";
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam String bucketName,@RequestParam String key) throws IOException{
        this.s3Services.downloadFile(bucketName, key);
        return ResponseEntity.ok("Archivo descargado correctamente");
    }

    @PostMapping("/upload/presigned")
    public ResponseEntity<String> generatedPresignedUploadUrl(@RequestParam String bucketName,@RequestParam String key,@RequestParam Long time){
        Duration durationToLive = Duration.ofMinutes(time);

        return ResponseEntity.ok(this.s3Services.generatePresignedUploadUrl(bucketName, key, durationToLive));
    }

    @PostMapping("/download/presigned")
    public ResponseEntity<String> generatedPresignedDownloaddUrl(@RequestParam String bucketName,@RequestParam String key,@RequestParam Long time){
        Duration durationToLive = Duration.ofMinutes(time);

        // falta implementar en el metodo que descargue en la carpeta static
        String url = this.s3Services.generatePresignedDownloadUrl(bucketName, key, durationToLive);
        System.out.println(url);
        return ResponseEntity.ok(url);
    }

}
