package io.yzecho.zrpc.consumer.controller;

import io.yzecho.zrpc.consumer.annotation.RpcReference;
import io.yzecho.zrpc.facade.HelloFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bc.yzecho
 */
@RestController
public class HelloController {

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
    private HelloFacade helloFacade;

    @RequestMapping(value = "/hello")
    public String hello(@RequestParam("name") String name) {
        return helloFacade.hello(name);
    }
}
