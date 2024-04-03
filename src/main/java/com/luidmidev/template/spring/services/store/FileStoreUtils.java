package com.luidmidev.template.spring.services.store;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.function.Consumer;

public class FileStoreUtils {

    private static final Tika TIKA = new Tika();

    private static final Logger logger = LoggerFactory.getLogger(FileStoreUtils.class);

    public static Boolean validateType(String contentTypePattern, String filename) {
        String detectedContentType = TIKA.detect(filename);
        logger.info("Detected content type: {}", detectedContentType);
        return detectedContentType.matches(contentTypePattern);
    }

    public static Consumer<HttpHeaders> getHeaders(String filename, Boolean inline) {
        var headerValue = (inline ? "inline" : "attachment") + "; filename=\"" + filename + "\"";
        return headers -> {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, headerValue);
            headers.set("X-Suggested-Filename", filename);
            headers.setAccessControlExposeHeaders(List.of(HttpHeaders.CONTENT_DISPOSITION, "X-Suggested-Filename"));
        };
    }

    public static Consumer<HttpHeaders> getHeaders(String filename) {
        return getHeaders(filename, false);
    }
}
