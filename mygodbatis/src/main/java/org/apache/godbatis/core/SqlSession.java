package org.apache.godbatis.core;

import javax.swing.text.StyleContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;

public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession() {
    }

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public int insert(String sqlId,Object bean) throws SQLException {
        int count=0;
        Map<String, MappedStatement> map = sqlSessionFactory.getMap();
        MappedStatement mappedStatement = map.get(sqlId);
        String mssql=mappedStatement.getSql();
//        insert into t_user(id,name) values(#{id},#{name})
        String sql=mssql.replaceAll("#\\{[a-zA-Z0-1]*}","?");
        Connection connection=sqlSessionFactory.getTransactionManager().getConnection();
        PreparedStatement ps = connection.prepareStatement("sql");
//        通过传递过来的对象来获取属性值，首先的首先，你得把#{}这里面的玩儿解析出来吧
        int fromindex=-1;
        int endindex=-1;
        int index=0;
        while(true){
            fromindex=mssql.indexOf("{",fromindex+1);
            if(fromindex==-1) break;;
            endindex=mssql.indexOf("}",endindex+1);
            String frag=mssql.substring(fromindex+1,endindex);
            String methodname="get"+frag.toUpperCase().substring(0,1)+frag.substring(1);
            Class<?> beanClass = bean.getClass();
//           我怎么拿到它的对象呢，我不知道它是什么类啊，我怎么强转呢
            try {
                Method method = beanClass.getMethod(methodname);
                Object propertyValue = method.invoke(bean);
                ps.setString(++index,propertyValue.toString());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        count=ps.executeUpdate();
        return count;
    }

    /**
     *
     * @param sqlId
     * @param param 根据什么查询
     * @return
     */
    public Object selectOne(String sqlId,Object param) throws SQLException {
        Object obj=null;
        Map<String,MappedStatement> map=sqlSessionFactory.getMap();
        MappedStatement mappedStatement = map.get(sqlId);
        String classType= mappedStatement.getResultType();
        String msSql=mappedStatement.getSql();
        String sql=msSql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
        PreparedStatement ps=sqlSessionFactory.getTransactionManager().getConnection().prepareStatement(sql);
        ps.setString(1,param.toString());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Class<?> resultTypeClass = null;
            try {
                resultTypeClass = Class.forName(classType);
                obj = resultTypeClass.newInstance();
                //                给实现类的属性赋值
//                给obj对象的哪个属性赋哪个值
                ResultSetMetaData rsmd = rs.getMetaData();//查询结果的原数据,里面就有列名
                int columnCount = rsmd.getColumnCount();
                for(int i=1;i<=columnCount;i++){
                    String propertyName = rsmd.getColumnName(i);
                    String setMethodName="set"+propertyName.toUpperCase().charAt(0)+propertyName.substring(1);
                    Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName, String.class);
                    setMethod.invoke(obj,rs.getString(propertyName));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }


        }
        return obj;
    }
    public void commit() throws SQLException {
        sqlSessionFactory.getTransactionManager().commit();
    }
    public void close() throws SQLException {
        sqlSessionFactory.getTransactionManager().close();
    }


}
