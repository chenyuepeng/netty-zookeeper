package com.fourfaith.comm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author chenyuepeng
 * @create 2018-02-02 11:01
 * @since: 1.0.0
 * @desc 配置文件读取
 **/
public class PropertyConfig {

    private static Map<String, String> config_map = new HashMap<String, String>();

    static {
        try {
            InputStream oInputStream = new FileInputStream("./conf/config.properties");
            Properties props = new Properties();
            props.load(oInputStream);

            Enumeration en = props.keys();
            while (en.hasMoreElements()) {
                String name = en.nextElement().toString();
                String value = props.getProperty(name);
                config_map.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * @desc
     * @author chenyuepeng
     * @date 2018/2/2 11:05
     */
    public static String getProperty(String key){
        return config_map.get(key);
    }
}
