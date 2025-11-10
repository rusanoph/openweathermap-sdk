package io.openweathermap.sdk.core.serializer;

public interface SerializerPort {

    <T> T fromJson(byte[] body, Class<T> clazz);

    byte[] toJson(Object object);
}
