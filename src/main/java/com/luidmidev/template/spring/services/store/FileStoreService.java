package com.luidmidev.template.spring.services.store;

import lombok.*;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileStoreService {

    Tika TIKA = new Tika();

    String store(MultipartFile upload) throws IOException;

    String store(InputStream upload, String filename) throws IOException;

    String storeEphimeral(InputStream upload, String filename) throws IOException;

    DownloadedFile download(String id) throws IOException;

    FileInfo info(String id) throws IOException;

    void remove(String id);

    default void purge(PurgableFileStore purgable) {
        for (var id : purgable.filesId()) remove(id);
    }

    default void purge(Iterable<? extends PurgableFileStore> purgables) {
        for (var purgable : purgables) purge(purgable);
    }

    default String guessContentType(String filename) {
        return TIKA.detect(filename);
    }


    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @Data
    class DownloadedFile extends FileInfo {
        private byte[] file;

        @Builder
        public DownloadedFile(String filename, String fileType, Long fileSize, byte[] file) {
            super(filename, fileType, fileSize);
            this.file = file;
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    class FileInfo {
        private String filename;
        private String fileType;
        private Long fileSize;
    }

}

