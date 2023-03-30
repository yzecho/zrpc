package io.yzecho.zrpc.protocol.serialization;

import lombok.Getter;

/**
 * @author bc.yzecho
 */
public enum SerializationTypeEnum {
    HESSIAN(0x10),
    JSON(0x20),
    PROTOBUF(0x30);

    @Getter
    private final int type;

    SerializationTypeEnum(int type) {
        this.type = type;
    }

    public static SerializationTypeEnum of(byte serializationType) {
        for (SerializationTypeEnum value : values()) {
            if (value.getType() == serializationType) {
                return value;
            }
        }
        return HESSIAN;
    }

}
