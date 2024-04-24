package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(String name);

    List<Authority> findAllByNameIn(List<String> names);

    boolean existsAllByNameIn(List<String> names);

    boolean existsByName(String name);

}
