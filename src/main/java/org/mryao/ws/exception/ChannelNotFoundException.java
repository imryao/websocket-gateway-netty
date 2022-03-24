package org.mryao.ws.exception;

public class ChannelNotFoundException extends WsException {

    public ChannelNotFoundException(String message) {
        super(message);
    }

    public ChannelNotFoundException(Throwable cause) {
        super(cause);
    }

    public ChannelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
