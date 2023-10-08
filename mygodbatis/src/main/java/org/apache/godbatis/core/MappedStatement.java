package org.apache.godbatis.core;

import java.util.Objects;

/**
 * 用来封装sqlMapper文件中的标签里面的内容
 */
public class MappedStatement {
    private String sql;
    private String resultType;

    public MappedStatement() {
    }

    public MappedStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MappedStatement)) return false;
        MappedStatement that = (MappedStatement) o;
        return Objects.equals(getSql(), that.getSql()) && Objects.equals(getResultType(), that.getResultType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSql(), getResultType());
    }

    @Override
    public String toString() {
        return "MappedStatement{" +
                "sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }
}
