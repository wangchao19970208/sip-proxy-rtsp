package com.shenzhen.teamway.db.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;

import java.io.Reader;
import java.util.Properties;

/**
 * <p>
 *
 * @author wangc
 * @date 2019/7/8 0008 11:47
 * @Description:
 */
public class DBConfig {
	private SqlSessionFactory sessionFactory;
	private SqlSession session;
	private static  volatile DBConfig dbConfig;
	public boolean init() {
		Properties properties = new Properties();
		ConfigManager instance = ConfigManager.instance();
		String url = instance.getValue("jdbc.url");
		String user = instance.getValue("jdbc.username");
		String password = instance.getValue("jdbc.password");
		properties.setProperty("jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("jdbc.url", url);
		properties.setProperty("jdbc.username", user);
		properties.setProperty("jdbc.password", password);

		properties.setProperty("poolPingEnabled", "true");
		properties.setProperty("poolPingConnectionsNotUsedFor", "10000");
		properties.setProperty("poolPingQuery", "select now()");

		try {
			Reader reader = Resources.getResourceAsReader("mybatis.cfg.xml");
			sessionFactory = new SqlSessionFactoryBuilder().build(reader, properties);
			session = sessionFactory.openSession();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public SqlSession getSession() {
		if (session == null) {
			session = SqlSessionManager.newInstance(sessionFactory);
		}
		return session;
	}

	public void commit() {
		session.commit();
	}

	private DBConfig(){

	}

	public static DBConfig getInstance(){
		if (dbConfig==null){
			synchronized (DBConfig.class){
				if (dbConfig==null){
					dbConfig=new DBConfig();
				}
			}
		}
		return  dbConfig;
	}


}
