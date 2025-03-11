package ru.alina.logstarter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.alina.logstarter.aspect.LoggingAspect;

@AutoConfiguration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "custom.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration {

    private final LoggingProperties loggingProperties;

    public LoggingAutoConfiguration(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(loggingProperties);
    }
}
