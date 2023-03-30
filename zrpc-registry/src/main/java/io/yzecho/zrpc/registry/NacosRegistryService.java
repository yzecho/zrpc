package io.yzecho.zrpc.registry;

import io.yzecho.zrpc.core.ServiceMeta;

/**
 * @author bc.yzecho
 */
public class NacosRegistryService implements RegistryService{

    public NacosRegistryService(String registryAddr) {

    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }
}
