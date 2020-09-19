package com.frt.BitconTrader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.manager.ParamManager;
import com.frt.BitconTrader.manager.PolicyManager;
import com.frt.BitconTrader.model.BarModel;
import com.frt.BitconTrader.model.OrderModel;
import com.frt.BitconTrader.model.TickerModel;

public class TestEntry {
	
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
	
	private HbdmswapClient m_client;
	private void initialClient() {
		m_client = new HbdmswapClient();
	}
	
	public TestEntry() {
		initialDatabase();
		initialParamManager();
		initialPolicyManager();
		initialClient();
	}
	
	public void startTest() {
		try {
			ArrayList<BarModel> all_bars = m_client.getSomeKLine(SystemConfig.PERIOD_1D, 1);
			System.out.println("bar= " + all_bars.get(0));
			TickerModel t_model = m_client.getTickerInfo();
			System.out.println("ticker= " + t_model);
			
			String buy_client = String.valueOf(System.currentTimeMillis());
			m_client.openOneOrder(String.valueOf(1.0), "1", SystemConfig.ORDER_OP_BUY, buy_client);
			OrderModel o_model = m_client.getOrderInfo(buy_client);
			System.out.println("order= " + o_model);
			//String sell_client = String.valueOf(System.currentTimeMillis());
			//m_client.openOneOrder(String.valueOf(1.0), "1", SystemConfig.ORDER_OP_SELL, sell_client);
			
			m_client.closeOneOrder(String.valueOf(1.0), "1", SystemConfig.ORDER_OP_SELL);//CLOSE BUY ORDER
			//m_client.closeOneOrder(String.valueOf(1.0), "1", SystemConfig.ORDER_OP_BUY);//CLOSE SELL ORDER
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
