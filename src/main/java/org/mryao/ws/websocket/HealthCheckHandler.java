package org.mryao.ws.websocket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.util.HttpResponseUtil;

@Sharable
@Slf4j
public class HealthCheckHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String websocketPath;

    public HealthCheckHandler(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        HttpHeaders headers = request.headers();
        if (HttpMethod.GET.equals(method) && websocketPath.equals(uri) && headers.contains(HttpHeaderNames.UPGRADE,
                HttpHeaderValues.WEBSOCKET, true)) {
            // websocket handshake
            ctx.fireChannelRead(request.retain());
        } else if (HttpMethod.HEAD.equals(method) && "/".equals(uri)) {
            // health check
            HttpResponseUtil.respond204(ctx, HttpUtil.isKeepAlive(request));
        } else {
            // undefined
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.NOT_FOUND);
        }
    }
}
