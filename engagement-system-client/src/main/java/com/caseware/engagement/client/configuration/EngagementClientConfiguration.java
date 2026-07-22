package com.caseware.engagement.client.configuration;

import com.caseware.engagement.client.entities.TemplateCatalog;
import com.caseware.engagement.client.mapper.UpdateDecisionMapper;
import com.caseware.engagement.client.repositories.EngagementTemplateStateRepository;
import com.caseware.engagement.client.repositories.TemplateCatalogRepository;
import com.caseware.engagement.client.repositories.UpdateDecisionRepository;
import com.caseware.engagement.client.services.UpdateDecisionService;
import com.caseware.engagement.client.services.impl.DefaultUpdateDecisionService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = TemplateCatalog.class)
@EnableJpaRepositories(basePackageClasses = TemplateCatalogRepository.class)
@ComponentScan(basePackageClasses = UpdateDecisionMapper.class)
public class EngagementClientConfiguration {
    @Bean
    UpdateDecisionService updateDecisionService(
            EngagementTemplateStateRepository stateRepository,
            TemplateCatalogRepository catalogRepository,
            UpdateDecisionRepository decisionRepository,
            UpdateDecisionMapper mapper) {
        return new DefaultUpdateDecisionService(
                stateRepository, catalogRepository, decisionRepository, mapper);
    }
}
