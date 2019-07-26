package com.shenzhen.teamway.sip.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/6/3 0003 15:09
 * @Description:读取外部配置文件工具类
 */
public class PropertiesUtils {
	Logger log = LoggerFactory.getLogger(PropertiesUtils.class);
	private static PropertiesUtils propertiesUtils = null;


	private String CONFIGFILE = "config.properties";

	static Properties p = new Properties();


	public boolean loadPropertiesFile(String path) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(path));
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("读取properties配置文件异常", e.getMessage());
			return false;
		}

		return true;
	}

	public String getValue(String key) {
		final String property = p.getProperty(key);
		if (property == null) {
			log.error("获取到的properties值为空，key: " + key);
		}

		log.debug("读取属性文件，" + key + '/' + property);
		return property;
	}

	public boolean readConfigFile() {
		String userDirPath = System.getProperty("user.dir");
		String configFile = userDirPath + File.separator +
				"config" + File.separator + CONFIGFILE;
		log.info("配置文件：" + configFile);
		return propertiesUtils.loadPropertiesFile(configFile);
	}

	public String getDbValue(String key) {
		if (key == null) {
			return null;
		}
		return propertiesUtils.getValue(key.trim());
	}

	private PropertiesUtils() {

	}

	public static PropertiesUtils getInstance() {
		if (propertiesUtils == null) {
			synchronized (PropertiesUtils.class) {
				if (propertiesUtils == null) {
					propertiesUtils = new PropertiesUtils();
					return propertiesUtils;
				}
			}
		}
		return propertiesUtils;
	}

}
