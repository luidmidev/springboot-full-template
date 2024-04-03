package com.luidmidev.template.spring.services.store.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "file_store")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileStore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content_length", nullable = false)
    private Long contentLength;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean ephimeral;

    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] content;
}