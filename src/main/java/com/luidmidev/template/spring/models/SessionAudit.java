package com.luidmidev.template.spring.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_audit")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Log4j2
public class SessionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String ip;

    private LocalDateTime datetime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {

        var request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert request != null;
        setIp(request.getRequest().getRemoteAddr());
        setDatetime(LocalDateTime.now());
        log.info("SessionAuditListener.onBeforeConvert: {}", this);
    }
}
