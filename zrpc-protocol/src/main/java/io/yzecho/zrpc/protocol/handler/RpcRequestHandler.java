package io.yzecho.zrpc.protocol.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.yzecho.zrpc.core.RpcRequest;
import io.yzecho.zrpc.core.RpcResponse;
import io.yzecho.zrpc.core.RpcServiceHelper;
import io.yzecho.zrpc.protocol.MsgStatus;
import io.yzecho.zrpc.protocol.MsgType;
import io.yzecho.zrpc.protocol.RpcProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author bc.yzecho
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Map<String, Object> serviceMap;

    public RpcRequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> protocol) throws Exception {
        RpcRequestProcessor.submitRequest(() -> {
            RpcProtocol.Header header = protocol.getHeader();
            // response 复用 header
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            RpcResponse.RpcResponseBuilder responseBuilder = RpcResponse.builder();
            RpcProtocol<RpcResponse> response = RpcProtocol.<RpcResponse>builder().build();
            try {
                // 处理请求
                Object result = handleRequest(protocol.getBody());
                header.setStatus((byte) MsgStatus.SUCCESS.getCode());

                response = RpcProtocol.<RpcResponse>builder().header(header)
                        .body(responseBuilder.data(result)
                                .build())
                        .build();
            } catch (Throwable throwable) {
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                responseBuilder.message(throwable.getMessage());
                log.error("process request: {} error", header.getRequestId(), throwable);
            }
            channelHandlerContext.writeAndFlush(response);
        });
    }

    private Object handleRequest(RpcRequest request) throws InvocationTargetException {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object serviceBean = serviceMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        FastClass fastClass = FastClass.create(serviceClass);
        int index = fastClass.getIndex(request.getMethodName(), request.getParameterTypes());
        return fastClass.invoke(index, serviceBean, request.getParams());
    }

}
