package org.mryao.ws.util;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;

public class HttpResponseUtil {

    public static void respond200(ChannelHandlerContext ctx, boolean keepAlive, String contentType,
            String payload) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.content().writeCharSequence(payload, StandardCharsets.UTF_8);
        HttpUtil.setContentLength(response, response.content().readableBytes());
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void respond204(ChannelHandlerContext ctx, boolean keepAlive) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NO_CONTENT);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void respondError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        ChannelFuture future = ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status));
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
