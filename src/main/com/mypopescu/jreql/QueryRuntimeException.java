package com.mypopescu.jreql;

/**
 * @author alex
 * @version 1.0, 5/19/13 12:24 AM
 */
public class QueryRuntimeException extends ReqlQueryException {
    protected QueryRuntimeException(String message, ReqlProto.Backtrace backtrace) {
        super(message, backtrace);
    }
}
