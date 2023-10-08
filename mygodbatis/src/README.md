第一步：参考使用mybatis的时候我们是如何进行CRUD的
SqlSession sqlSession=null;
try {
SqlSessionFactoryBuilder sqlSessionFactoryBuilder=new SqlSessionFactoryBuilder();
InputStream is= Resources.getResourceAsStream("mybatis-config.xml");
SqlSessionFactory sqlSessionFactory=sqlSessionFactoryBuilder.build(is);
sqlSession=sqlSessionFactory.openSession();
int count=sqlSession.insert("insertCar");
System.out.println(count);
} catch (IOException e) {
if (sqlSession != null) {
sqlSession.rollback();
}
throw new RuntimeException(e);
}finally{
if (sqlSession != null) {
sqlSession.close();
}
}
第二步：参照第一行，首先先new一个SqlSessionFactoryBuilder。那么我们就先创建一个SqlSessionFactoryBuilder类
第三步：我们要考虑SqlSessionFactoryBuilder里面有什么属性，有什么方法，一看就是builder方法，里面要传进去一个输入流,然后这个方法返回一个SqlSessionFactory对象
第四步：猜想是不是传过去一个输入流，然后解析xml文件呢，那我们找一个样例嘛

    