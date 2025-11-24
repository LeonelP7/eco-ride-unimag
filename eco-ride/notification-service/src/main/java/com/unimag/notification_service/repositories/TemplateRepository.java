package com.unimag.notification_service.repositories;

import com.unimag.notification_service.entities.Template;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TemplateRepository extends R2dbcRepository<Template, Integer> {
    Mono<Template> findByCode(String code);

}
