package io.yzecho.zrpc.protocol.serialization;

import java.io.IOException;

/**
 * @author bc.yzecho
 */
public interface RpcSerialization {
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
