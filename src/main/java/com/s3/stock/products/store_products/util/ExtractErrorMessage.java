package com.s3.stock.products.store_products.util;

import org.springframework.stereotype.Component;

@Component
public class ExtractErrorMessage {

        public String extractErrorMessage(String originalMessage) {
            if (originalMessage == null || originalMessage.isEmpty()) {
                return "Validation error";
            }
    
            int firstMessageEndIndex = originalMessage.indexOf("default message [");
            if (firstMessageEndIndex == -1) {
                return "Validation error";
            }
            firstMessageEndIndex = originalMessage.indexOf("]", firstMessageEndIndex) +1;
    
            int messageStartIndex = originalMessage.indexOf("default message [", firstMessageEndIndex);
            if (messageStartIndex == -1) {
                return "Validation error";
            }
    
            messageStartIndex += "default message [".length();
            int messageEndIndex = originalMessage.indexOf("]", messageStartIndex);
            if (messageEndIndex == -1) {
                return "Validation error";
            }
    
            return originalMessage.substring(messageStartIndex, messageEndIndex);

    }
}