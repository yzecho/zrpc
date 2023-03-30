package io.yzecho.zrpc.registry;

import io.yzecho.zrpc.core.RpcServiceHelper;
import io.yzecho.zrpc.core.ServiceMeta;
import io.yzecho.zrpc.registry.lb.ConsistentHashLoadBalancer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;

/**
 * @author bc.yzecho
 */
public class ZookeeperRegistryService implements RegistryService {

    public static final int BASE_SLEEP_TIME_MS = 1000;

    public static final int MAX_RETRIES = 3;

    public static final String BASE_PATH = "/zrpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    private final ConsistentHashLoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    public ZookeeperRegistryService(String registryAddr) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();

        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> instance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(instance);
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> instance = ServiceInstance.<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(instance);
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> serviceInstance = loadBalancer.select((List<ServiceInstance<ServiceMeta>>) serviceInstances, invokerHashCode);
        if (serviceInstance != null) {
            return serviceInstance.getPayload();
        }
        return null;
    }

    @Override
    public void destroy() throws Exception {
        serviceDiscovery.close();
    }
}
