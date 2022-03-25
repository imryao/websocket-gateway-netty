package org.mryao.ws;

import lombok.extern.slf4j.Slf4j;
import org.mryao.ws.http.HttpServerInitializer;
import org.mryao.ws.websocket.WebSocketServerInitializer;

@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {

        NettyServer wsServer = new NettyServer("ws-server", 8080, new WebSocketServerInitializer("/gw"));
        NettyServer httpServer = new NettyServer("http-server", 8081, new HttpServerInitializer("/channels/"));

        Thread wsThread = new Thread(wsServer::start, "ws-thread");
        Thread httpThread = new Thread(httpServer::start, "http-thread");

        wsThread.start();
        httpThread.start();

        log.info("websocket-gateway started");
    }
}
