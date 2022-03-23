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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.util.IdUtil;

/**
 * Echoes uppercase content of text frames.
 */
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        String id = ctx.channel().id().asLongText();
        log.info("{} channelRead0", id);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        String id = ctx.channel().id().asLongText();
        log.info("{} channelRead", id);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} handlerAdded", id);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} channelRegistered", id);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} channelActive", id);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} channelInactive", id);
        ChannelManager.removeByValue(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} channelUnregistered", id);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        String id = ctx.channel().id().asLongText();
        log.info("{} handlerRemoved", id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        String id = ctx.channel().id().asLongText();
        log.warn("{} exceptionCaught", id, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        Channel channel = ctx.channel();
        String id = channel.id().asLongText();
        if (evt instanceof HandshakeComplete) {
            log.info("{} userEventTriggered HandshakeComplete", id);
            String key = IdUtil.getRandomString();
            channel.writeAndFlush(new TextWebSocketFrame(key));
            ChannelManager.put(key, channel);
        } else {
            log.info("{} userEventTriggered other", id);
        }
    }
}
