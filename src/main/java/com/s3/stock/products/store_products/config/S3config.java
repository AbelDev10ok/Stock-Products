package com.s3.stock.products.store_products.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3config {

    @Value("${aws.acces.key}")
    private String awsAccesKey;
    @Value("${aws.secret.key}")
    private String awsSecretKey;
    @Value("${aws.region}")
    private String region;

    // cliente s3 Syncrono
    @Bean    
    public S3Client getS3Client(){
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccesKey, awsSecretKey);
        return S3Client.builder()       
                .region(Region.of(region))
                .endpointOverride(URI.create("http://s3.us-east-1.amazonaws.com"))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

    // cliente s3 Asyncrono
    @Bean    
    public S3AsyncClient getS3AsyncClient(){
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccesKey, awsSecretKey);
        return S3AsyncClient.builder()       
                .region(Region.of(region))
                .endpointOverride(URI.create("http://s3.us-east-1.amazonaws.com"))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

    // url prefirmadas, podemos dar permisos por unos cuantos minutos
    @Bean
    public S3Presigner getS3Presigner(){
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccesKey, awsSecretKey);
        return S3Presigner.builder()       
                .region(Region.of(region))
                .endpointOverride(URI.create("http://s3.us-east-1.amazonaws.com"))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }
}
