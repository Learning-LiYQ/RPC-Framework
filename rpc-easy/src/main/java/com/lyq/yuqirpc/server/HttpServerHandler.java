package com.lyq.yuqirpc.server;

import com.lyq.yuqirpc.model.RpcRequest;
import com.lyq.yuqirpc.model.RpcResponse;
import com.lyq.yuqirpc.registry.LocalRegistry;
import com.lyq.yuqirpc.serializer.JdkSerializer;
import com.lyq.yuqirpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HTTP 请求处理
 * @author lyq
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Serializer serializer;

    public HttpServerHandler() {
        // todo:暂时使用JDK序列化器
        this.serializer = new JdkSerializer();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 检查解码结果
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        // 只处理POST请求
//        if (request.method() != HttpMethod.POST) {
//            sendError(ctx, METHOD_NOT_ALLOWED);
//            return;
//        }

        try {
            // 获取请求体
            ByteBuf content = request.content();
            byte[] bodyBytes = new byte[content.readableBytes()];
            content.readBytes(bodyBytes);

            // 反序列化请求
            RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
            if (rpcRequest == null) {
                sendError(ctx, BAD_REQUEST);
                return;
            }

            // 获取要调用的服务实现类，通过反射调用
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
            // 封装返回结果
            RpcResponse rpcResponse = RpcResponse.builder()
                    .data(result)
                    .dataType(method.getReturnType())
                    .message("Success")
                    .build();

            doResponse(ctx, rpcResponse, OK);

        } catch (Exception e) {
            log.error("处理RPC请求失败", e);
            RpcResponse errorResponse = RpcResponse.builder()
                    .message("Server error")
                    .exception(e)
                    .build();

            doResponse(ctx, errorResponse, INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 处理响应
     * @param ctx 通道上下文
     * @param rpcResponse RPC响应对象
     * @param status HTTP状态码
     */
    private void doResponse(ChannelHandlerContext ctx, RpcResponse rpcResponse, HttpResponseStatus status) throws Exception {
        byte[] responseBytes = serializer.serialize(rpcResponse);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(responseBytes));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream")
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Server exception:", cause);
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                status,
                Unpooled.copiedBuffer("Error: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
