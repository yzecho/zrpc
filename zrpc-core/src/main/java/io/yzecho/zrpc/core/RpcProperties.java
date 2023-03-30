package io.yzecho.zrpc.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author bc.yzecho
 */
@Data
@ConfigurationProperties(prefix = "zrpc")
public class RpcProperties {
    // 服务暴露的端口
    private int servicePort;
    // 注册中心的地址
    private String registryAddr;
    // 注册中心的类型
    private String registryType;
}
