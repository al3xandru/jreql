package com.mypopescu.jreql;

/**
 * An exception thrown when there's an I/O issue connecting
 * or communicating with the server.
 */
public class ConnectionException extends ReqlException {
    public ConnectionException(String msg, Object data) {
        super(msg, data);
    }

    public ConnectionException(Throwable cause, Object data) {
        super(cause, data);
    }

    public ConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConnectionException(String msg, Throwable cause, Object data) {
        super(msg, cause, data);
    }
}
