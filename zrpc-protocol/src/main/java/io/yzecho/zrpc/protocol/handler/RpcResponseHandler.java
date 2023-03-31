package io.yzecho.zrpc.protocol.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.yzecho.zrpc.core.RpcFuture;
import io.yzecho.zrpc.core.RpcRequestHolder;
import io.yzecho.zrpc.core.RpcResponse;
import io.yzecho.zrpc.protocol.RpcProtocol;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author bc.yzecho
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> msg) throws Exception {
        long requestId = msg.getHeader().getRequestId();
        // 收到响应后将映射关系溢出
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        // 通过 promise 将 res 写回
        future.getPromise().setSuccess(msg.getBody());
    }
}


