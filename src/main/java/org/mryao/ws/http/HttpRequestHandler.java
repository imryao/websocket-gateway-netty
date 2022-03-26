package org.mryao.ws.http;

import static org.mryao.ws.ChannelManager.KEY_PATTERN;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.ChannelManager;
import org.mryao.ws.MessageTypeEnum;
import org.mryao.ws.util.HttpResponseUtil;

@Sharable
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String channelUriPrefix;

    private final Pattern channelUriPattern;

    public HttpRequestHandler(String channelUriPrefix) {
        this.channelUriPrefix = channelUriPrefix;
        this.channelUriPattern = Pattern.compile(channelUriPrefix + KEY_PATTERN);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!request.decoderResult().isSuccess()) {
            // handle bad request
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.BAD_REQUEST);
        } else if (HttpMethod.GET.equals(method) && channelUriPattern.matcher(uri).matches()) {
            // check channel
            log.info("channelRead {} {} {}", method, uri, keepAlive);
            String key = getChannelKeyFromUri(uri);
            checkChannel(ctx, keepAlive, key);
        } else if (HttpMethod.POST.equals(method) && channelUriPattern.matcher(uri).matches()) {
            // send message
            String payload = request.content().toString(StandardCharsets.UTF_8);
            log.info("channelRead {} {} {} {}", method, uri, keepAlive, payload);
            String key = getChannelKeyFromUri(uri);
            sendMessage(ctx, keepAlive, key, payload);
        } else if (HttpMethod.DELETE.equals(method) && channelUriPattern.matcher(uri).matches()) {
            // close channel
            log.info("channelRead {} {} {}", method, uri, keepAlive);
            String key = getChannelKeyFromUri(uri);
            closeChannel(ctx, keepAlive, key);
        } else {
            // not found
            log.info("channelRead {} {} {}", method, uri, keepAlive);
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.NOT_FOUND);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.warn("exceptionCaught", cause);
    }

    private void checkChannel(ChannelHandlerContext ctx, boolean keepAlive, String key) {
        Channel channel = ChannelManager.get(key);
        if (channel == null) {
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.NOT_FOUND);
        } else {
            HttpResponseUtil.respond204(ctx, keepAlive);
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, boolean keepAlive, String key, String message) {
        if (message == null || message.length() == 0) {
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.BAD_REQUEST);
        } else {
            Channel channel = ChannelManager.get(key);
            if (channel == null) {
                HttpResponseUtil.respondError(ctx, HttpResponseStatus.NOT_FOUND);
            } else {
                ChannelManager.sendMessage(channel, MessageTypeEnum.DATA, message);
                HttpResponseUtil.respond204(ctx, keepAlive);
            }
        }
    }

    private void closeChannel(ChannelHandlerContext ctx, boolean keepAlive, String key) {
        Channel channel = ChannelManager.get(key);
        if (channel == null) {
            HttpResponseUtil.respondError(ctx, HttpResponseStatus.NOT_FOUND);
        } else {
            channel.close();
            HttpResponseUtil.respond204(ctx, keepAlive);
        }
    }

    private String getChannelKeyFromUri(String uri) {
        return uri.substring(channelUriPrefix.length());
    }
}
