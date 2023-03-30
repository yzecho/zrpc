package io.yzecho.zrpc.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bc.yzecho
 */
@Data
@NoArgsConstructor
public class ServiceMeta {
    private String serviceName;
    private String serviceVersion;
    private String serviceAddr;
    private int servicePort;
}
