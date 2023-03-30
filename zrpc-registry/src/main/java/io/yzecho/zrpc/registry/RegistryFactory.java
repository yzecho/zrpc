package io.yzecho.zrpc.registry;

/**
 * @author bc.yzecho
 */
public class RegistryFactory {

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddr, RegistryType registryType) throws Exception {
        if (registryService == null) {
            synchronized (RegistryFactory.class) {
                if (registryService == null) {
                    switch (registryType) {
                        case ZOOKEEPER -> registryService = new ZookeeperRegistryService(registryAddr);
                        case NACOS -> registryService = new NacosRegistryService(registryAddr);
                    }
                }
            }
        }
        return registryService;
    }
}
