package com.s3.stock.products.store_products.controllers.advices;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.s3.stock.products.store_products.util.ErrorResponse;
import com.s3.stock.products.store_products.util.ExtractErrorMessage;

import jakarta.persistence.EntityNotFoundException;

// @ControllerAdvice
public class GlobalExceptionHandler {

    // @Autowired
    // private ExtractErrorMessage extractErrorMessage;

    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
    //     return ResponseEntity.badRequest().body(new ErrorResponse("400", e.getMessage()));
    // }

    // @ExceptionHandler(EntityNotFoundException.class)
    // public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    // }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<?> handleException(Exception e, WebRequest request) {
    //     // System.out.println("\n\n\n\n"+e.getMessage()+ "\n\n \n exce");
    //     // String message = extractErrorMessage.extractErrorMessage(e.getMessage());
    //     return ResponseEntity.internalServerError().body(new ErrorResponse("500", e.getMessage()));
    // }

    // @ExceptionHandler(IOException.class)
    // public ResponseEntity<?> handleIOException(IOException e, WebRequest request) {
    //     String message = extractErrorMessage.extractErrorMessage(e.getMessage());
    //     return ResponseEntity.internalServerError().body(message);
    // }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
    //     System.out.println("\n\n\n\n"+e.getMessage()+ "\n\n \n exce");
    //     String message = extractErrorMessage.extractErrorMessage(e.getMessage());
    //     return ResponseEntity.badRequest().body(new ErrorResponse("400", message));
    // }
}
