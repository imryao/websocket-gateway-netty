package org.mryao.ws.util;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseUtil {

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
