package com.mypopescu.jreql;

/**
 * @author alex
 * @version 1.0, 5/19/13 12:22 AM
 */
public class QueryClientException extends ReqlQueryException {
    protected QueryClientException(String message, ReqlProto.Backtrace backtrace) {
        super(message, backtrace);
    }
}
