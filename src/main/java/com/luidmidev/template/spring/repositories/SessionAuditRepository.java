package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.models.SessionAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SessionAuditRepository extends MongoRepository<SessionAudit, Long> {
}
