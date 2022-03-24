package org.mryao.ws.exception;

public class JacksonException extends WsException {

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
    }
}
