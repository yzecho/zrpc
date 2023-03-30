package io.yzecho.zrpc.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.SocketUtils;
import io.yzecho.zrpc.core.RpcServiceHelper;
import io.yzecho.zrpc.core.ServiceMeta;
import io.yzecho.zrpc.core.utils.JvmUtils;
import io.yzecho.zrpc.protocol.codec.RpcDecoder;
import io.yzecho.zrpc.protocol.codec.RpcEncoder;
import io.yzecho.zrpc.protocol.handler.RpcRequestHandler;
import io.yzecho.zrpc.provider.annotation.RpcService;
import io.yzecho.zrpc.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bc.yzecho
 */
@Slf4j
public class ServiceProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;
    private final int serverPort;
    private final RegistryService registryService;

    private final Map<String, Object> serviceMap = new HashMap<>();

    public ServiceProvider(int serverPort, RegistryService registryService) {
        this.serverPort = serverPort;
        this.registryService = registryService;
    }

    private void start() throws Exception {
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(JvmUtils.availableProcessors());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(SocketUtils.socketAddress(serverAddress, serverPort))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcRequestHandler(serviceMap));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("Server addr {} started on port {}", this.serverAddress, this.serverPort);
                }
            });
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();
            try {
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(serverPort);
                serviceMeta.setServiceName(serviceName);
                serviceMeta.setServiceVersion(serviceVersion);

                // 服务注册
                registryService.register(serviceMeta);
                // 服务关联
                serviceMap.put(RpcServiceHelper.buildServiceKey(serviceName, serviceVersion), bean);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceName, serviceVersion, e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                start();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        }).start();
    }
}
