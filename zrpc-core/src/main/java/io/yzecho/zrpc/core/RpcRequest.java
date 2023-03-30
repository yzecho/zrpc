package io.yzecho.zrpc.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bc.yzecho
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
