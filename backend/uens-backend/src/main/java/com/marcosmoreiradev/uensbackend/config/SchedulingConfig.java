package com.marcosmoreiradev.uensbackend.config;

import com.marcosmoreiradev.uensbackend.config.properties.ReportQueueProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
/**
 * Define la responsabilidad de SchedulingConfig dentro del backend UENS.
 * Contexto: modulo core, capa config, arquitectura monolito modular Spring Boot.
 * Alcance: centralizar configuracion transversal del contexto Spring Boot.
 */
public class SchedulingConfig {

    @Bean(name = "reportQueueTaskScheduler")
    @ConditionalOnProperty(prefix = "app.report.queue", name = "enabled", havingValue = "true", matchIfMissing = true)
/**
 * Implementa la operacion 'reportQueueTaskScheduler' del modulo core en la capa config.
 * Contexto: flujo funcional de fase 1 UENS con contrato API y reglas de negocio documentadas.
 *
     * @param properties propiedades tipadas que gobiernan limites y frecuencia de procesamiento
     * @return resultado de la operacion dentro de la arquitectura modular del backend UENS.
 */
    public ThreadPoolTaskScheduler reportQueueTaskScheduler(ReportQueueProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Math.max(1, properties.schedulerPoolSize()));
        scheduler.setThreadNamePrefix("report-queue-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(15);
        return scheduler;
    }
}

