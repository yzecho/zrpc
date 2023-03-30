package io.yzecho.zrpc.core;

/**
 * @author bc.yzecho
 */
public final class RpcServiceHelper {
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("#", serviceName, serviceVersion);
    }
}
