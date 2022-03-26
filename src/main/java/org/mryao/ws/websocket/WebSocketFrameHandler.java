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

package org.mryao.ws.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.ChannelManager;
import org.mryao.ws.MessageTypeEnum;
import org.mryao.ws.util.IdUtil;

@Sharable
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        // ping and pong frames already handled

        if (msg instanceof TextWebSocketFrame frame) {
            Channel channel = ctx.channel();
            String key = ChannelManager.getByValue(channel);
            String request = frame.text();
            log.info("channelRead0 {} {}", key, request);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String key = ChannelManager.removeByValue(ctx.channel());
        ctx.close();
        log.info("handlerRemoved {}", key);
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String key = ChannelManager.removeByValue(ctx.channel());
        ctx.close();
        log.warn("exceptionCaught {}", key, cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String key = IdUtil.getRandomString();
            Channel channel = ctx.channel();
            ChannelManager.put(key, channel);
            ChannelManager.sendMessage(channel, MessageTypeEnum.KEY, key);
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
            String ip = remoteAddress.getAddress().getHostAddress();
            int port = remoteAddress.getPort();
            log.info("HandshakeComplete {} from {}:{}", key, ip, port);
        } else if (evt instanceof IdleStateEvent event) {
            switch (event.state()) {
                case READER_IDLE -> log.info("READER_IDLE");
                case WRITER_IDLE -> {
                    ctx.writeAndFlush(new PingWebSocketFrame());
                    log.info("WRITER_IDLE, pinging");
                }
                case ALL_IDLE -> log.info("ALL_IDLE");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
