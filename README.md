SARC is a rest based service for accessing a data store, initially  we support only Cassandra and Mysql
This project introduces a metadata layer between a datastore and a client by using relational concepts to model a no-sql store.

For more information http://github.com/netflix/sarc/wiki

This is the first release of Staash and it is currently being used in a limited way within netflix.

Some features provided in SARC

    o Highe level rest interface for cassandra.
    o High level rest inteface for mysql.
    o Ability to hide complexity behind relational concepts by modeling data in relational    terms.
    o Ability to create and define storage.
    o Ability to create and list database,table,timeseries.
    o Ability to read and write to a Cassandra storage w/o knowing connection information.
    o Ablity to be able to join a dataset across storage systems.
    o Ability to creat, read and write to a timeseries.


