package com.s3.stock.products.store_products.services.interfaces;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Services {
    // crear bucket en s3
    String createBucket(String bucketName);

    // Saber si un bucket existe
    String checkIfBucketExist(String bucketName);

    // Listar buckets
    List<String> getAllBuckets();

    // Cargar un archivo a un bucket
    // Boolean uploadFile(String nucketName, String key, Path fileLocation);

    String uploadFile(MultipartFile file) throws IOException;

    
    // Descargar un archivo de un bucket
    void downloadFile(String bucket, String key) throws IOException;

    // Generar URL prefirmada para subir archivos 
    // son urls que generamos para conseder permiso temporal a una app o un usuario
    String generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    // Generar URL prefirmada para descargar archivis
    String generatePresignedDownloadUrl(String bucketName, String key, Duration duration);


    void deleteFile(String fileUrl) throws IOException ;

}
