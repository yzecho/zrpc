package io.yzecho.zrpc.consumer;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.yzecho.zrpc.core.RpcFuture;
import io.yzecho.zrpc.core.RpcRequest;
import io.yzecho.zrpc.core.RpcRequestHolder;
import io.yzecho.zrpc.core.RpcResponse;
import io.yzecho.zrpc.protocol.MsgType;
import io.yzecho.zrpc.protocol.ProtocolConstants;
import io.yzecho.zrpc.protocol.RpcProtocol;
import io.yzecho.zrpc.protocol.serialization.SerializationTypeEnum;
import io.yzecho.zrpc.registry.RegistryFactory;
import io.yzecho.zrpc.registry.RegistryService;
import io.yzecho.zrpc.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author bc.yzecho
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;

    private String registryAddr;

    private long timeout;

    private Object object;

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddr, RegistryType.valueOf(this.registryType));
        this.object = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
            RpcRequest r = RpcRequest.builder()
                    .serviceVersion(this.serviceVersion)
                    .className(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .params(args)
                    .parameterTypes(method.getParameterTypes())
                    .build();
            RpcProtocol.Header header = RpcProtocol.Header.builder()
                    .magic(ProtocolConstants.MAGIC)
                    .version(ProtocolConstants.VERSION)
                    .requestId(requestId)
                    .serialization((byte) SerializationTypeEnum.HESSIAN.getType())
                    .msgType((byte) MsgType.REQUEST.getType())
                    .status((byte) 0x1)
                    .build();
            RpcProtocol<RpcRequest> request = RpcProtocol.<RpcRequest>builder()
                    .header(header)
                    .body(r)
                    .build();

            RpcConsumer consumer = new RpcConsumer();
            RpcFuture<RpcResponse> responseFuture = RpcFuture.<RpcResponse>builder()
                    .promise(new DefaultPromise<>(new DefaultEventLoop()))
                    .timeout(timeout)
                    .build();
            // 请求响应映射
            RpcRequestHolder.REQUEST_MAP.put(requestId, responseFuture);

            // 发送 rpc 请求
            consumer.sendRequest(request, registryService);
            return responseFuture.getPromise().get(responseFuture.getTimeout(), TimeUnit.MILLISECONDS).getData();
        });
    }
}
