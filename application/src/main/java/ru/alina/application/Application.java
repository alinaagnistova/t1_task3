package ru.alina.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import ru.alina.application.config.EmailProperties;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(EmailProperties.class)
@Import(ru.alina.logstarter.config.LoggingAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
