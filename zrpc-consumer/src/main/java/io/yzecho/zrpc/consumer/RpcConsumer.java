package io.yzecho.zrpc.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.yzecho.zrpc.core.RpcRequest;
import io.yzecho.zrpc.core.RpcServiceHelper;
import io.yzecho.zrpc.core.ServiceMeta;
import io.yzecho.zrpc.protocol.RpcProtocol;
import io.yzecho.zrpc.protocol.codec.RpcDecoder;
import io.yzecho.zrpc.protocol.codec.RpcEncoder;
import io.yzecho.zrpc.protocol.handler.RpcResponseHandler;
import io.yzecho.zrpc.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bc.yzecho
 */
@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    public void sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = request.getParams();
        int invokerHashCode = params.length > 0 ? params[0].hashCode() : registryService.hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMeta != null) {
            ChannelFuture future = bootstrap.connect(serviceMeta.getServiceAddr(), serviceMeta.getServicePort()).sync();
            future.addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    log.info("Connect zrpc server {} on port {} success.", serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                } else {
                    log.error("Connect zrpc server {} on port {} failed.", serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });
            future.channel().writeAndFlush(protocol);
        }
    }

}
