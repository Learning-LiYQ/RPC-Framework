package com.lyq.yuqirpc.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HTTP 请求处理
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 处理HTTP请求
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        // 创建HTTP响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer("Hello, World!", CharsetUtil.UTF_8));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        // 发送响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status,
                Unpooled.copiedBuffer("Error: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
