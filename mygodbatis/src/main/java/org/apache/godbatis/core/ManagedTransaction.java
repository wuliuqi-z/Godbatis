package org.apache.godbatis.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ManagedTransaction implements TransactionManager{
    private Connection connection;
    private DataSource dataSource;
    @Override
    public Connection getConnection() throws SQLException {
        if(connection==null){
            dataSource.getConnection();
        }
        return connection;
    }

    @Override
    public void commit() {

    }

    @Override
    public void close() {

    }
}
