package ru.alina.application.kafka.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, byte[] data) {
        try{
            return super.deserialize(topic, data);
        }catch(Exception e){
            log.warn("Error during deserialize message {}", new String(data, StandardCharsets.UTF_8),e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try{
            return super.deserialize(topic, headers, data);
        }catch(Exception e){
            log.warn("Error during deserialize message {}", new String(data, StandardCharsets.UTF_8),e);
            return null;
        }
    }

}
