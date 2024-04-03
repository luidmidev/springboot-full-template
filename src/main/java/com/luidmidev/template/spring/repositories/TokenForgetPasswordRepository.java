package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.models.TokenForgetPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenForgetPasswordRepository extends MongoRepository<TokenForgetPassword, String> {
    Logger logger = LoggerFactory.getLogger(TokenForgetPasswordRepository.class);

    Optional<TokenForgetPassword> findByToken(String token);

    default void deleteExpiredToken() {
        var tokens = findAll();
        var expired = tokens.stream().filter(TokenForgetPassword::isExpired).toList();
        deleteAll(expired);
        logger.info("Has been deleted {} expired tokens", expired.size());
    }
}

