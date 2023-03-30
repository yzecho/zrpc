package io.yzecho.zrpc.protocol.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import io.yzecho.zrpc.protocol.exception.SerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author bc.yzecho
 */
@Slf4j
@Component
public class HessianSerialization implements RpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            throw new NullPointerException();
        }

        HessianSerializerOutput output;
        byte[] result;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            output = new HessianSerializerOutput(os);
            output.writeObject(obj);
            output.flush();
            result = os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        if (data == null) {
            throw new NullPointerException();
        }

        HessianSerializerInput input;
        T result;
        try (ByteArrayInputStream os = new ByteArrayInputStream(data)) {
            input = new HessianSerializerInput(os);
            result = (T) input.readObject(clazz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return result;
    }
}
