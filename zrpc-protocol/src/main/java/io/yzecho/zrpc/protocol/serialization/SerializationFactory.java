package io.yzecho.zrpc.protocol.serialization;

/**
 * @author bc.yzecho
 */
public class SerializationFactory {

    public static RpcSerialization getSerialization(byte serializationType) {
        SerializationTypeEnum serializationTypeEnum = SerializationTypeEnum.of(serializationType);
        switch (serializationTypeEnum) {
            case HESSIAN -> {
                return new HessianSerialization();
            }
            default -> throw new IllegalArgumentException("serialization type is illegal, " + serializationType);
        }
    }

}
