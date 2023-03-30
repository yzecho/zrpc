package io.yzecho.zrpc.provider.facade;

import io.yzecho.zrpc.facade.HelloFacade;
import io.yzecho.zrpc.provider.annotation.RpcService;

/**
 * @author bc.yzecho
 */
@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "1.0.0")
public class HelloFacadeImpl implements HelloFacade {

    @Override
    public String hello(String name) {
        return String.format("hello %s", name);
    }
}
