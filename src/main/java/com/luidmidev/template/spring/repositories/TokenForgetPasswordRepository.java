package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.models.TokenForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenForgetPasswordRepository extends JpaRepository<TokenForgetPassword, String> {

    Optional<TokenForgetPassword> findByToken(String token);

    default void deleteExpiredToken() {
        var tokens = findAll();
        var expired = tokens.stream().filter(TokenForgetPassword::isExpired).toList();
        deleteAll(expired);
    }
}

