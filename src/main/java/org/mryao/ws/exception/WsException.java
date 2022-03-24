package org.mryao.ws.exception;

public class WsException extends RuntimeException {

    public WsException(String message) {
        super(message);
    }

    public WsException(Throwable cause) {
        super(cause);
    }

    public WsException(String message, Throwable cause) {
        super(message, cause);
    }
}
