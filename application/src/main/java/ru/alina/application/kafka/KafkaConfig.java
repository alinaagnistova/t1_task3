package ru.alina.application.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.alina.application.dto.TaskDto;
import ru.alina.application.kafka.util.MessageDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.group-id}")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Bean
    public ConsumerFactory<String, TaskDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TaskDto.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class.getName());

        DefaultKafkaConsumerFactory<String, TaskDto> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskDto> kafkaListenerContainerFactory(ConsumerFactory<String, TaskDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TaskDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        errorHandler.setRetryListeners(((record, ex, deliveryAttempt) -> {
            log.error(" RetryListeners message = {}, offset = {}, deliveryAttempt = {}", ex.getMessage(), record.offset(), deliveryAttempt);
        }
        ));
        return errorHandler;
    }

    @Bean(name = "task_updating")
    public KafkaTemplate<String, TaskDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, TaskDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);

    }

}
