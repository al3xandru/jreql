package com.mypopescu.jreql;

import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hamcrest.CoreMatchers;

/**
 * @author alex
 * @version 1.0, 5/19/13 2:42 AM
 */
public class TestParseResponse {
    @Test
    public void clientException() {
        try {
            ReqlProto.Response.Builder b= ReqlProto.Response.newBuilder()
                .setType(ReqlProto.Response.ResponseType.CLIENT_ERROR)
                .addResponse(ReqlProto.Datum.newBuilder().setRStr("client error").build());

            Connection connection = new Connection("example.org", 28015);
            ResultSet rs = connection.parseResponse(null,
                ReqlProto.Query.QueryType.START,
                null,
                -1,
                b.build());
            fail(format("Expected %s exception", QueryClientException.class));
        }
        catch(QueryClientException ex) {
            assertThat(ex.getMessage(), is("client error"));
        }
    }


    @Test
    public void compileException() {
        try {
            ReqlProto.Response.Builder b= ReqlProto.Response.newBuilder()
                .setType(ReqlProto.Response.ResponseType.COMPILE_ERROR)
                .addResponse(ReqlProto.Datum.newBuilder().setRStr("compile error").build());

            Connection connection = new Connection("example.org", 28015);
            ResultSet rs = connection.parseResponse(null,
                ReqlProto.Query.QueryType.START,
                null,
                -1,
                b.build());
            fail(format("Expected %s exception", QueryCompileException.class));
        }
        catch(QueryCompileException ex) {
            assertThat(ex.getMessage(), is("compile error"));
        }
    }

    @Test
    public void runtimeException() {
        try {
            ReqlProto.Response.Builder b= ReqlProto.Response.newBuilder()
                .setType(ReqlProto.Response.ResponseType.RUNTIME_ERROR)
                .addResponse(ReqlProto.Datum.newBuilder().setRStr("runtime error").build());

            Connection connection = new Connection("example.org", 28015);
            ResultSet rs = connection.parseResponse(null,
                ReqlProto.Query.QueryType.START,
                null,
                -1,
                b.build());
            fail(format("Expected %s exception", QueryRuntimeException.class));
        }
        catch(QueryRuntimeException ex) {
            assertThat(ex.getMessage(), is("runtime error"));
        }
    }

    @Test
    public void nullResult() {
        ReqlProto.Response.Builder b= ReqlProto.Response.newBuilder()
            .setType(ReqlProto.Response.ResponseType.SUCCESS_ATOM)
            ;

        Connection connection = new Connection("example.org", 28015);
        ResultSet rs = connection.parseResponse(null,
            ReqlProto.Query.QueryType.START,
            null,
            -1,
            b.build());

        assertTrue(rs.isEmpty());
        assertFalse(rs.next());
        assertFalse(rs.first());
    }


}
