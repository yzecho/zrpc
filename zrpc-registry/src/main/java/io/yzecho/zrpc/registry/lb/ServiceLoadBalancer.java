package io.yzecho.zrpc.registry.lb;

import java.util.List;

/**
 * @author bc.yzecho
 */
public interface ServiceLoadBalancer<T> {
    T select(List<T> servers, int hashCode);
}
