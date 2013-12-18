As the complexity of the data land-scape grows the application developers are left to wrestle with a lot of details that they should be immune to. Buzzwords like no-sql,k-v store, document storage etc are confusing to  grapple with for a dev who has been purely working on relational technologies. Staash is a rest based service for accessing a data store, it is an ambitious project but some of the initial aims of the project are geared towards automating the common data access patterns and hiding the complexity of underlying system for developers. To that end in this initial release   we  offer implementation of a metadata layer and pattern automation and corresponding apis for  Cassandra and Mysql(just for proof).


For more information http://github.com/Netflix/staash/wiki

Link to the netflix tech blog http://techblog.netflix.com

This is the first release of Staash and it is currently being used in a limited way within netflix.

Some features provided in Staash

    o High level rest interface for cassandra.
    o High level rest interface for mysql.
    o Ability to hide complexity behind relational concepts by modeling data in relational terms.
    o Ability to create and define storage.
    o Ability to create and list databases, tables and event-series(in development).
    o Ability to read and write to a Cassandra storage without knowing connection information.
    o Ability to be able to join a dataset across storage systems.
    o Ability to create, read and write to a simple event-series(in development).
    o Ability to create, read and write to a prefix event series(in development).
    o Provide auto-sharding for temporal data for event-series.
    o Simple REST API for a key-value store(in progress).
