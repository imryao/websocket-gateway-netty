/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.mryao.ws;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.util.IdUtil;
import org.slf4j.MDC;

/**
 * Echoes uppercase content of text frames.
 */
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // ping and pong frames already handled

        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("WebSocketFrameHandler.handlerAdded");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("WebSocketFrameHandler.channelRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("WebSocketFrameHandler.channelActive");
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("WebSocketFrameHandler.channelRead");
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("WebSocketFrameHandler.channelReadComplete");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String key = ChannelManager.removeByValue(ctx.channel());
        log.info("WebSocketFrameHandler.channelInactive, removing {}", key);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("WebSocketFrameHandler.channelUnregistered");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("WebSocketFrameHandler.handlerRemoved");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("WebSocketFrameHandler.exceptionCaught", cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel channel = ctx.channel();
        String id = channel.id().asLongText();
        if (evt instanceof HandshakeComplete) {
            String key = IdUtil.getRandomString();
            MDC.put("channelId", id);
            MDC.put("channelKey", key);
            log.info("WebSocketFrameHandler.userEventTriggered HandshakeComplete");
            channel.writeAndFlush(new TextWebSocketFrame(key));
            ChannelManager.put(key, channel);
        } else if (evt instanceof IdleStateEvent event) {
            switch (event.state()) {
                case READER_IDLE -> log.info("READER_IDLE");
                case WRITER_IDLE -> {
                    ctx.writeAndFlush(new PingWebSocketFrame());
                    log.info("WRITER_IDLE");
                }
                case ALL_IDLE -> log.info("ALL_IDLE");
            }
        }
    }
}
