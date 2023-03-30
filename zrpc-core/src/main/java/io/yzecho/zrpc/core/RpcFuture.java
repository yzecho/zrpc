package io.yzecho.zrpc.core;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bc.yzecho
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcFuture<T> {
    private Promise<T> promise;
    private long timeout;
}
