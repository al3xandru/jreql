package com.mypopescu.jreql;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author alex
 * @version 1.0, 5/19/13 3:41 AM
 */
public class TestRandomQueries {
    @Test
    public void count() {
        ReqlProto.Term.Builder term = ReqlProto.Term.newBuilder()
            .setType(ReqlProto.Term.TermType.COUNT);
        term.addArgs(
            ReqlProto.Term.newBuilder()
                .setType(ReqlProto.Term.TermType.TABLE)
                .addArgs(
                    ReqlProto.Term.newBuilder()
                        .setType(ReqlProto.Term.TermType.DB)
                        .addArgs(ReqlProto.Term.newBuilder()
                            .setType(ReqlProto.Term.TermType.DATUM)
                            .setDatum(ReqlProto.Datum.newBuilder()
                                .setType(ReqlProto.Datum.DatumType.R_STR)
                                .setRStr("test")
                                .build())
                            .build())
                        .build()
                )
                .addArgs(ReqlProto.Term.newBuilder()
                    .setType(ReqlProto.Term.TermType.DATUM)
                    .setDatum(ReqlProto.Datum.newBuilder()
                        .setType(ReqlProto.Datum.DatumType.R_STR)
                        .setRStr("t")
                        .build())
                    .build())
        );

        Connection c = new Connection("192.168.97.162", 28015).connect();
        ResultSet rs = c.run(term.build(), ReqlProto.Query.QueryType.START, null);
        rs.next();

        assertEquals(3, rs.getInt());
    }

    @Test
    public void tableCreate() {
        ReqlProto.Term.Builder term = ReqlProto.Term.newBuilder()
            .setType(ReqlProto.Term.TermType.TABLE_CREATE);
        term.addArgs(
            ReqlProto.Term.newBuilder()
                .setType(ReqlProto.Term.TermType.DB)
                .addArgs(ReqlProto.Term.newBuilder()
                    .setType(ReqlProto.Term.TermType.DATUM)
                    .setDatum(ReqlProto.Datum.newBuilder()
                        .setType(ReqlProto.Datum.DatumType.R_STR)
                        .setRStr("test")
                        .build())
                    .build())
                .build()
        );
        term.addArgs(ReqlProto.Term.newBuilder()
            .setType(ReqlProto.Term.TermType.DATUM)
            .setDatum(ReqlProto.Datum.newBuilder()
                .setType(ReqlProto.Datum.DatumType.R_STR)
                .setRStr("new_table")
                .build())
            .build());


        Connection c = new Connection("192.168.97.162", 28015).connect();
        ResultSet rs = c.run(term.build(), ReqlProto.Query.QueryType.START, null);
        rs.next();

        JsonObject obj= rs.getObj();

        assertNotNull(obj);
        assertEquals(1, obj.getInt("created"));
    }
}
