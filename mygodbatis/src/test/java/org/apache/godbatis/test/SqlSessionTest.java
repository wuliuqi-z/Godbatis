package org.apache.godbatis.test;

import org.apache.godbatis.core.MappedStatement;
import org.apache.godbatis.core.SqlSession;
import org.apache.godbatis.core.SqlSessionFactory;
import org.apache.godbatis.core.SqlSessionFactoryBuilder;
import org.apache.godbatis.utils.Resources;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class SqlSessionTest {
    @Test
    public void testInsert() throws SQLException {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder=new SqlSessionFactoryBuilder();
        SqlSessionFactory sq = sqlSessionFactoryBuilder.build(Resources.getSourceAsStream("godbatis-config.xml"));
        SqlSession sqlSession = sq.openSession();
        int count=0;
        Map<String, MappedStatement> map = sqlSession.getSqlSessionFactory().getMap();
        MappedStatement mappedStatement = map.get("CarMapper.insertCar");
        String mssql=mappedStatement.getSql();
//        insert into t_user(id,name) values(#{id},#{name})
        String sql=mssql.replaceAll("#\\{[a-zA-Z0-1]*}","?");
        Connection connection=sq.getTransactionManager().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
//        通过传递过来的对象来获取属性值，首先的首先，你得把#{}这里面的玩儿解析出来吧
        int fromindex=-1;
        int endindex=-1;
        int index=0;
        Car bean=new Car("1111","比亚迪汉");
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
                System.out.println(propertyValue);
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
    }
}
