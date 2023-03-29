package io.yzecho.zrpc.provider;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @author bc.yzecho
 */
@Slf4j
public class ServiceProvider {

    private String serverAddress;
    private int serverPort;


    private void startServer() {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
    }

}
