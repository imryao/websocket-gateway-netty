package org.mryao.ws;

import static org.mryao.ws.ChannelManager.KEY_PATTERN;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String CHANNEL_URI_PREFIX = "/channels/";

    private static final Pattern CHANNEL_URI_PATTERN = Pattern.compile(CHANNEL_URI_PREFIX + KEY_PATTERN);

    private final String websocketUri;

    public HttpRequestHandler(String webSocketUri) {
        this.websocketUri = webSocketUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.debug("HttpRequestHandler.channelRead0");
        HttpMethod method = request.method();
        String uri = request.uri();
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (HttpMethod.HEAD.equals(method) && "/".equals(uri)) {
            // http health check
            respond204(ctx, keepAlive);
        } else if (HttpMethod.GET.equals(method) && websocketUri.equals(uri)) {
            // websocket handshake
            ctx.fireChannelRead(request.retain());
        } else if (HttpMethod.GET.equals(method) && CHANNEL_URI_PATTERN.matcher(uri).matches()) {
            // check channel
            log.info("HttpRequestHandler.channelRead {} {} {}", method, uri, keepAlive);
            String key = getChannelKeyFromUri(uri);
            checkChannel(ctx, keepAlive, key);
        } else if (HttpMethod.POST.equals(method) && CHANNEL_URI_PATTERN.matcher(uri).matches()) {
            // send message
            log.info("HttpRequestHandler.channelRead {} {} {}", method, uri, keepAlive);
            String key = getChannelKeyFromUri(uri);
            String payload = request.content().toString();
            sendMessage(ctx, keepAlive, key, payload);
        } else if (HttpMethod.DELETE.equals(method) && CHANNEL_URI_PATTERN.matcher(uri).matches()) {
            // close channel
            log.info("HttpRequestHandler.channelRead {} {} {}", method, uri, keepAlive);
            String key = getChannelKeyFromUri(uri);
            closeChannel(ctx, keepAlive, key);
        } else {
            // not found
            log.info("HttpRequestHandler.channelRead {} {} {}", method, uri, keepAlive);
            respond404(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("HttpRequestHandler.exceptionCaught", cause);
    }

    private void checkChannel(ChannelHandlerContext ctx, boolean keepAlive, String key) {
        Channel channel = ChannelManager.get(key);
        if (channel == null) {
            respond404(ctx);
        } else {
            respond204(ctx, keepAlive);
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, boolean keepAlive, String key, String message) {
        Channel channel = ChannelManager.get(key);
        if (channel == null) {
            respond404(ctx);
        } else {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            respond204(ctx, keepAlive);
        }
    }

    private void closeChannel(ChannelHandlerContext ctx, boolean keepAlive, String key) {
        Channel channel = ChannelManager.remove(key);
        if (channel == null) {
            respond404(ctx);
        } else {
            channel.close();
            respond204(ctx, keepAlive);
        }
    }

    private String getChannelKeyFromUri(String uri) {
        return uri.substring(CHANNEL_URI_PREFIX.length());
    }

    private void respond204(ChannelHandlerContext ctx, boolean keepAlive) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT));
        if (!keepAlive) {
            ctx.close();
        }
    }

    private void respond404(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
        ctx.close();
    }
}
