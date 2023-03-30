package io.yzecho.zrpc.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bc.yzecho
 */
@Data
@Builder
public class RpcResponse implements Serializable {
    private Object data;
    private String message;
}
