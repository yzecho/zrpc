package io.yzecho.zrpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.yzecho.zrpc.core.RpcRequest;
import io.yzecho.zrpc.core.RpcResponse;
import io.yzecho.zrpc.protocol.MsgType;
import io.yzecho.zrpc.protocol.ProtocolConstants;
import io.yzecho.zrpc.protocol.RpcProtocol;
import io.yzecho.zrpc.protocol.serialization.RpcSerialization;
import io.yzecho.zrpc.protocol.serialization.SerializationFactory;

import java.util.List;

/**
 * @author bc.yzecho
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }

        in.markReaderIndex();
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();
        int dataLength = in.readInt();

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        MsgType mType = MsgType.of(msgType);
        if (mType == null) {
            return;
        }

        RpcProtocol.Header header = RpcProtocol.Header.builder()
                .magic(magic)
                .version(version)
                .serialization(serializeType)
                .status(status)
                .requestId(requestId)
                .msgType(msgType)
                .msgLen(dataLength)
                .build();

        RpcSerialization serialization = SerializationFactory.getSerialization(serializeType);

        switch (mType) {
            case REQUEST -> {
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<Object> protocol = RpcProtocol.builder()
                            .header(header)
                            .body(request)
                            .build();
                    out.add(protocol);
                }
            }
            case RESPONSE -> {
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<Object> protocol = RpcProtocol.builder()
                            .header(header)
                            .body(response)
                            .build();
                    out.add(protocol);
                }
            }
            case HEARTBEAT -> {

            }
        }
    }
}
