package com.luidmidev.template.spring.services;


import com.luidmidev.template.spring.models.SessionAudit;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.repositories.SessionAuditRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionAuditService {

    private final SessionAuditRepository repository;

    public SessionAuditService(SessionAuditRepository repository) {
        this.repository = repository;
    }

    public void saveActionUser(User user, String description) {
        var audit = SessionAudit.builder()
                .user(user)
                .description(description)
                .build();
        repository.save(audit);
    }

}
