package com.caseware.engagement.client.repositories;

import com.caseware.engagement.client.entities.TemplateCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateCatalogRepository extends JpaRepository<TemplateCatalog, String> {
}
