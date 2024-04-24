package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.models.SessionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SessionAuditRepository extends JpaRepository<SessionAudit, Long> {
}
