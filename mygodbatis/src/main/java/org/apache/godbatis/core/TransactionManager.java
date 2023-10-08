package org.apache.godbatis.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器接口
 */
public interface TransactionManager {
    Connection getConnection() throws SQLException;
    void commit() throws SQLException;
    void close() throws SQLException;
}
