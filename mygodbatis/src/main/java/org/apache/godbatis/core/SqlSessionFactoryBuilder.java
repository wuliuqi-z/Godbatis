package org.apache.godbatis.core;

import org.apache.godbatis.utils.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {
    }
    public SqlSessionFactory build(InputStream is){
//        解析is流中的文件内容，然后当new SqlSessionFactory的时候，将这些重要的内容封装到这个对象里面
        SqlSessionFactory sqlSessionFactory=null;
        try {
            SAXReader reader=new SAXReader();
            Document document=reader.read(is);
            Element rootElement = document.getRootElement();
            String xpath="/configuration/environments";
            Element environments =(Element) rootElement.selectSingleNode(xpath);
            String def=environments.attributeValue("default");
            xpath="/configuration/environments/environment[@id='"+def +"']";
            Element environmentElt = (Element) environments.selectSingleNode(xpath);
//        先解析到账号和密码，然后创建数据源对象的时候需要账号和密码，然后创建事务管理器对象的时候需要数据源对象
            Element datasource = environmentElt.element("dataSource");//数据源
//            获取数据源对象
            DataSource dataSource = getDataSource(datasource);
//            获取事务管理器对象
            String managerType=environmentElt.element("transactionManager").attributeValue("type");
            TransactionManager transactionManager = getTransactionManager(dataSource, managerType);
//            还有一个没有完成，我们缺少new SqlSessionFactory的大map，所以我们要解析sqlMapper文件
//            首先我们要获取sqlMapper xml文件的具体名字
            List<Node> list = rootElement.selectNodes("//mapper");
            List<String> list1=new ArrayList<>();
            list.forEach(l->{
                list1.add(((Element)l).attributeValue("resource"));
            });
            Map<String, MappedStatement> map = getMap(list1);
            sqlSessionFactory=new SqlSessionFactory(map,transactionManager);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return sqlSessionFactory;
    }
    public Map<String,MappedStatement> getMap(List<String> list){
        Map<String,MappedStatement> map=new HashMap<>();
        list.forEach(l->{
            try {
                SAXReader reader=new SAXReader();
                Document document=reader.read(Resources.getSourceAsStream(l));
                Element rootElement = document.getRootElement();
                String namespace=rootElement.attributeValue("namespace");
                List elements = rootElement.elements();
                elements.forEach(sn->{
                    Element snEle=(Element) sn;
                    String id=snEle.attributeValue("id");
                    String type=snEle.attributeValue("resultType");
                    map.put((namespace+"."+id),new MappedStatement(snEle.getTextTrim(),type));
                });
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });
        return map;
    }
    public DataSource getDataSource(Element datasource){
        String sourceType=datasource.attributeValue("type");
        DataSource dataSource=null;
//        获取数据源的账号和密码
        List properties = datasource.elements("property");
        Map<String,String> map=new HashMap<>();
        properties.forEach(property->{
            Element pro=(Element)property;
            map.put(pro.attributeValue("name"),pro.attributeValue("value"));//获取键值对，classname=password
        });
        String driver=map.get("driver");
        String url=map.get("url");
        String username=map.get("username");
        String password=map.get("password");
        if(sourceType.equals(Const.UNPOOLED_DATA_SOURCE)){
            dataSource=new UnpooledDataSource(driver,url,username,password);
        }
        return dataSource;
    }
    public TransactionManager getTransactionManager(DataSource dataSource,String transactionType){
        TransactionManager transactionManager=null;
        if(transactionType.equals(Const.JDBC_TRANSACTION_MANAGER)){
            transactionManager=new JdbcTransaction(dataSource);
        }
        return transactionManager;
    }

//    public static void main(String[] args) throws DocumentException {
//
//    }
}
