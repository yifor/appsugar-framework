package org.appsugar.framework.serializable.spring.redis;

import org.appsugar.framework.serializable.AppsugarSerializable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class AppsugarRedisSerializer<T> implements RedisSerializer<T> {

    private AppsugarSerializable serializable;

    public AppsugarRedisSerializer(AppsugarSerializable serializable) {
        this.serializable = serializable;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return serializable.asByteArray(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return (T) serializable.asObject(bytes);
    }
}
