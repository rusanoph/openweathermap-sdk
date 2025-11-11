package io.openweathermap.sdk.adapters.serializer.jackson;

import io.openweathermap.sdk.core.serializer.SerializerPort;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public class JacksonSerializerAdapter implements SerializerPort {

    private final ObjectMapper objectMapper;

    public JacksonSerializerAdapter() {
        this(JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build());
    }

    @Override
    public <T> T fromJson(byte[] body, Class<T> clazz) {
        return objectMapper.readValue(body, clazz);
    }

    @Override
    public byte[] toJson(Object object) {
        return objectMapper.writeValueAsBytes(object);
    }
}
