package io.yzecho.zrpc.registry;

import io.yzecho.zrpc.core.ServiceMeta;

/**
 * @author bc.yzecho
 */
public interface RegistryService {

    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    void destroy() throws Exception;

}
