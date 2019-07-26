
package com.shenzhen.teamway.db.config;

import com.shenzhen.teamway.sip.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigManager {
    private Logger log=LoggerFactory.getLogger(getClass());
    private static volatile ConfigManager cm ;
    private final String CONFIGFILE = "config.properties";
    private final PropertiesUtils propertiesUtils =PropertiesUtils.getInstance();



    public static ConfigManager instance() {
        if (cm == null) {
            synchronized (ConfigManager.class) {
                if (cm == null) {
                    cm = new ConfigManager();
                }
            }
        }
        return cm;
    }
    private ConfigManager(){
        readConfigFile();
    }
        public boolean readConfigFile() {
        String userDirPath = System.getProperty("user.dir");
        String configFile = userDirPath + File.separator +
                "config" + File.separator + CONFIGFILE;
        log.info("配置文件：" + configFile);
        return propertiesUtils.loadPropertiesFile(configFile);
    }

    public String getValue(String key) {
        if (key == null) {
            return null;
        }
        return propertiesUtils.getValue(key.trim());
    }
}
