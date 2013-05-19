package com.mypopescu.jreql;

/**
 * The root of all jreql exceptions.
 */
public class ReqlException extends RuntimeException {
    protected Object m_data;

    public ReqlException(String msg, Object data) {
        super(msg);
        m_data= data;
    }

    public ReqlException(Throwable cause, Object data) {
        super(cause);
        m_data= data;
    }

    public ReqlException(String msg, Throwable cause) {
        this(msg, cause, null);
    }

    public ReqlException(String msg, Throwable cause, Object data) {
        super(msg, cause);
        m_data= data;
    }

    public Object getData() {
        return m_data;
    }

    @SuppressWarnings("InfiniteRecursion")
    @Override
    public String getMessage() {
        String msg= super.getMessage();
        Throwable cause= getCause();

        if(null != cause) {
            StringBuilder sb= new StringBuilder();
            if(null != msg) {
                sb.append(msg).append("; ");
            }
            sb.append("nested exception: ").append(cause);

            return sb.toString();
        }

        return msg;
    }
}
