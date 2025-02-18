package com.lyq.yuqirpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lyq.yuqirpc.model.RpcRequest;
import com.lyq.yuqirpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo序列化器
 * @author lyq
 */
@Slf4j
public class KryoSerializer implements Serializer{
    /**
     * Kryo是非线程安全的，使用threadlocal来保证每个线程只有一个Kryo实例
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false); // 关闭引用跟踪（提高性能，但需确保无循环引用）
        kryo.setRegistrationRequired(false); // 允许未注册的类
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)) {
            // Object->byte:将对象序列化为byte数组
            KRYO_THREAD_LOCAL.get().writeObject(output, obj);
            output.close();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            T result = KRYO_THREAD_LOCAL.get().readObject(input, clazz);
            input.close();
            return result;
        }
    }
}
