package org.mryao.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    private final String name;

    private final int port;

    private final ChannelHandler childHandler;

    public NettyServer(String name, int port, ChannelHandler childHandler) {
        this.name = name;
        this.port = port;
        this.childHandler = childHandler;
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(childHandler);

            Channel ch = b.bind(port).sync().channel();

            log.info("{} started", name);

            ch.closeFuture().sync();
        } catch (Exception e) {
            log.error("{} start fail", name, e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
