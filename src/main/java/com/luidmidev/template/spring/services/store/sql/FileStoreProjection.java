package com.luidmidev.template.spring.services.store.sql;

import java.util.UUID;

public interface FileStoreProjection {
    UUID getId();

    Long getContentLength();

    String getContentType();

    String getOriginalFileName();
}
