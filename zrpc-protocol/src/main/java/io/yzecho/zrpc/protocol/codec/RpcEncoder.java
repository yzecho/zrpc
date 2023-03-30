package io.yzecho.zrpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.yzecho.zrpc.protocol.RpcProtocol;
import io.yzecho.zrpc.protocol.serialization.RpcSerialization;
import io.yzecho.zrpc.protocol.serialization.SerializationFactory;

/**
 * @author bc.yzecho
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> message, ByteBuf byteBuf) throws Exception {
        RpcProtocol.Header header = message.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());

        RpcSerialization serialization = SerializationFactory.getSerialization(header.getSerialization());
        Object body = message.getBody();
        byte[] data = serialization.serialize(body);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }

}
