**Note**: This project is work in progress. As features are added
these will be documented here.

A Java driver for [RethinkDB](http://www.rethinkdb.com), the open source distributed JSON
document database with a pleasant and powerful query language.

## What works ##

1.  The Ant build

    1.  fetches the latest protobuf file from [RethinkDB's next branch](https://raw.github.com/rethinkdb/rethinkdb/next/src/rdb_protocol/ql2.proto)
   and generates the sources
    2.  compile, etc.

2.  [Connection](https://github.com/al3xandru/jreql/blob/master/src/main/com/mypopescu/jreql/Connection.java): sends,
    receives, and parses responses.

3.  [ResultSet](https://github.com/al3xandru/jreql/blob/master/src/main/com/mypopescu/jreql/Connection.java) for
    handling result.

4.  Converting the ProtoBuf `Datum` to native types
    (see [DatumConverter](https://github.com/al3xandru/jreql/blob/master/src/main/com/mypopescu/jreql/internal/DatumConverter.java))

## What's next ##

1.  Admin API (DB, Table, Index)
2.  Basic query API
3.  Filtering with `Criteria`
4.  Lambda-functions for `filter`, `map`, etc.



