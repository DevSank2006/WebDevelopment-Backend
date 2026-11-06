package com.webdev.webdevelopment.Services;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository
        extends JpaRepository<ServiceEntity, Long> {
}