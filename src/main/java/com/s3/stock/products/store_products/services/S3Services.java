package com.s3.stock.products.store_products.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
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
        try {
            this.s3Client.headBucket(headBucket -> headBucket.bucket(bucketName));
            return "El bucket existe";
        } catch (S3Exception e) {
            return "El bucket no existe";
        }

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



    @Override
    public Boolean uploadFile(String nucketName, String key, Path fileLocation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(nucketName)
            .key(key)
            .build();
        PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest, fileLocation);
        return putObjectResponse.sdkHttpResponse().isSuccessful();
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
