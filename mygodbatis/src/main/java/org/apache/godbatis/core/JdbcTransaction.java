package org.apache.godbatis.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements TransactionManager{
    private Connection connection;
    private DataSource dataSource;
    private boolean autoCommit;

    public JdbcTransaction() {
    }

    public JdbcTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public JdbcTransaction(DataSource dataSource,boolean autoCommit){
        this.dataSource=dataSource;
        this.autoCommit=autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(connection==null){
            connection=dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
