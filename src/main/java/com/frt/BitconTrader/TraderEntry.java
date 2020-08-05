package com.frt.BitconTrader;

import java.sql.Connection;
import java.sql.DriverManager;

import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.manager.ParamManager;
import com.frt.BitconTrader.manager.PolicyManager;

public class TraderEntry {
	
	private Connection m_db_connection;
	private void initialDatabase() {
		try {
			Class.forName(SystemConfig.mysql_driver);
			m_db_connection = DriverManager.getConnection(SystemConfig.mysql_url, SystemConfig.user_name, SystemConfig.password);
			if(!m_db_connection.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			} else {
				System.out.println("False connecting to the Database!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private ParamManager m_param_manager;
	private void initialParamManager() {
		m_param_manager = new ParamManager(m_db_connection);
	}
	
	private PolicyManager m_policy_manager;
	private void initialPolicyManager() {
		m_policy_manager = new PolicyManager(m_param_manager);
	}
	
	public TraderEntry() {
		initialDatabase();
		initialParamManager();
		initialPolicyManager();
	}
	
	public void startTrade() {
		m_policy_manager.startManager();
	}
}
