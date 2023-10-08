package org.apache.godbatis.utils;

import java.io.InputStream;

/**
 * godbatis工具类
 */
public class Resources {
    /**
     * 工具类分构造方法一般都是私有化，因为它不需要new对象，是直接通过类名调用方法
     */
    private Resources(){}

    /**
     * 默认从类的根路径下根据文件名寻找文件，然后返回一个输入流对象
     * @param filename
     * @return
     */
    public static InputStream getSourceAsStream(String filename){
        return ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
    }
}
