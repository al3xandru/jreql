package com.mypopescu.jreql;

/**
 * @author alex
 * @version 1.0, 5/19/13 12:39 AM
 */
public class ReqlQueryException extends ReqlException {
    protected final ReqlProto.Backtrace m_backtrace;

    protected ReqlQueryException(String message, ReqlProto.Backtrace backtrace) {
        super(message, null);
        m_backtrace = backtrace;
    }
}
