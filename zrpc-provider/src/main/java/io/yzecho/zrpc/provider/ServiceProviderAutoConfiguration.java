package io.yzecho.zrpc.provider;

import io.yzecho.zrpc.core.RpcProperties;
import io.yzecho.zrpc.registry.RegistryFactory;
import io.yzecho.zrpc.registry.RegistryService;
import io.yzecho.zrpc.registry.RegistryType;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bc.yzecho
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class ServiceProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public ServiceProvider init() throws Exception {
        int servicePort = rpcProperties.getServicePort();
        String registryAddr = rpcProperties.getRegistryAddr();
        RegistryType registryType = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService registryService = RegistryFactory.getInstance(registryAddr, registryType);
        return new ServiceProvider(servicePort, registryService);
    }
}
