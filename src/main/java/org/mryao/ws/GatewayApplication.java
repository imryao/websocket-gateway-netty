package org.mryao.ws;

import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.http.HttpRequestHandler;
import org.mryao.ws.websocket.WebSocketRequestHandler;

@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {

        NettyServer wsServer = new NettyServer("ws-server", 8080,
                new HttpServerInitializer(new WebSocketRequestHandler("/gw")));
        NettyServer httpServer = new NettyServer("http-server", 8081,
                new HttpServerInitializer(new HttpRequestHandler("/channels")));

        Thread wsThread = new Thread(wsServer::start, "ws-thread");
        Thread httpThread = new Thread(httpServer::start, "http-thread");

        wsThread.start();
        httpThread.start();

        log.info("websocket-gateway started");
    }
}
