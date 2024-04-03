package com.luidmidev.template.spring.services.store.sql;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileStoreRepository extends JpaRepository<FileStore, UUID> {

    void deleteAllByEphimeralTrue();

    Optional<FileStoreProjection> findProjectedById(UUID id);

}