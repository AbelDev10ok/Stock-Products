package com.s3.stock.products.store_products.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.s3.stock.products.store_products.services.interfaces.IS3Services;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3Services implements IS3Services{

    @Autowired
    private S3Client s3Client;

    @Value("${spring.destination.folder}")
    private String destinatioFolder;
    
    @Autowired
    private S3Presigner s3Presigner;

    @Override
    public String createBucket(String bucketName) {    
        CreateBucketResponse response = this.s3Client.createBucket( bucketBuilder -> bucketBuilder.bucket(bucketName));
        return "Bucket creado en la ubicacion " + response.location();
    }

    @Override
    public String checkIfBucketExist(String bucketName) {
        ListBucketsResponse listBucketsResponse = this.s3Client.listBuckets();
        System.out.println("sdasdasdasdsa"+bucketName);
        boolean bucketExists = listBucketsResponse.buckets()
            .stream()
            .anyMatch(bucket -> bucket.name().equals(bucketName));
    
        return bucketExists ? "El bucket existe" : "El bucket no existe";

    }

    @Override
    public List<String> getAllBuckets() {
        ListBucketsResponse bucketResponse = this.s3Client.listBuckets();
        if(bucketResponse.hasBuckets()){
            return bucketResponse.buckets()
                .stream()
                .map(bucket -> bucket.name())
                .toList();
        }else{
            return List.of();
        }
    }



    // @Override
    // public Boolean uploadFile(String bnucketName, String key, Path fileLocation) {

    //     PutObjectRequest putObjectRequest = PutObjectRequest.builder()
    //         .bucket(bnucketName)
    //         .key(key)
    //         .build();
    //     PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest, fileLocation);
    //     return putObjectResponse.sdkHttpResponse().isSuccessful();
    // }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        try {
            // Extraer el nombre del archivo (key) desde la URL
            String bucketName = "control-stock-electro123"; // Nombre de tu bucket
            String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // Eliminar el archivo de S3
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
        } catch (S3Exception e) {
            throw new IOException("Error al eliminar el archivo de S3: " + e.getMessage(), e);
        }
    }
    
    @Override
        public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("control-stock-electro123")
                .key(fileName)
                // en este caso le coloco este contenttype para que la ruta que me de no la descargue sino que me muestre la imagen
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        String s =s3Client.utilities().getUrl(builder -> builder.bucket("control-stock-electro123").key(fileName)).toString();
        System.out.println(s);
        return s;
    }

    

    @Override
    public void downloadFile(String bucket, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(getObjectRequest);
        String fileName;
        if(key.contains("/")){
            fileName = key.substring(key.lastIndexOf("/"));
        }else{
            fileName = key;
        }

        String filePath = Paths.get(destinatioFolder,fileName).toString();

        // reviso si el directorio existe si no existe lo creo.
        File file = new File(filePath);
        file.getParentFile().mkdir();

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){           
            fileOutputStream.write(objectBytes.asByteArray());
        }catch (IOException e){
            throw new IOException("Eror al descargar el archivo ",e.getCause());
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(duration)
            .putObjectRequest(putObjectRequest)
            .build();

        // el objeto que contiene la url que nos devuelve aws
        PresignedPutObjectRequest presignedPutRequest = this.s3Presigner.presignPutObject(presignRequest);
        URL presignedUrl = presignedPutRequest.url();
        return presignedUrl.toString();
    }

    @Override
    public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(duration)
            .getObjectRequest(getObjectRequest)
            .build();
        
        PresignedGetObjectRequest presignedRequest = this.s3Presigner.presignGetObject(presignRequest);
        URL presignedUrl = presignedRequest.url();
        return presignedUrl.toString();
    }

}
