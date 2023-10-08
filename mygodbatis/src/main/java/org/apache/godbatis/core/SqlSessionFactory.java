package org.apache.godbatis.core;

import java.util.Map;

/**
 * 此对象包含xml的所有内容
 */
public class SqlSessionFactory {
    private Map<String,MappedStatement> map;

    private TransactionManager transactionManager;

    public SqlSessionFactory(Map<String, MappedStatement> map, TransactionManager transactionManager) {
        this.map = map;
        this.transactionManager = transactionManager;
    }

    public SqlSessionFactory() {
    }
    public SqlSession openSession(){
        return new SqlSession(this);
    }

    public Map<String, MappedStatement> getMap() {
        return map;
    }

    public void setMap(Map<String, MappedStatement> map) {
        this.map = map;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
