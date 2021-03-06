package net.industryhive.common.config;

import net.industryhive.common.system.Constant;
import org.apache.commons.configuration2.PropertiesConfiguration;

import java.io.*;
import java.util.Properties;

/**
 * 配置文件加载工具类
 *
 * @Author 未央
 * @Create 2020-10-04 21:28
 */
public class PropertiesLoader {
    /**
     * 加载properties文件并根据key获取value
     *
     * @param key 键
     * @return value 值
     */
    public static String getValue(String key) {
        File file = new File(Constant.CURRENT_CONF);
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

    /**
     * 加载properties文件并根据key获取value数组
     *
     * @param key 键
     * @return 值
     */
    public static String[] getValueArray(String key) {
        File file = new File(Constant.CURRENT_CONF);
        FileReader fileReader = null;
        PropertiesConfiguration prop;
        try {
            fileReader = new FileReader(file);
            prop = new PropertiesConfiguration();
            prop.read(fileReader);
            return prop.getStringArray(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
