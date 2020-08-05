package com.frt.BitconTrader.inf;

import java.util.ArrayList;

import com.frt.BitconTrader.HbdmswapClient;
import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.manager.ParamManager;
import com.frt.BitconTrader.model.BarModel;
import com.frt.BitconTrader.model.BitconParamModel;
import com.frt.BitconTrader.model.TickerModel;

public class AbsPolicy implements PolicyInterface {

	protected int m_param_id;
	protected String m_current_client_id;
	protected ParamManager m_param_manger;
	protected HbdmswapClient m_client;
	protected BitconParamModel m_current_param;
	protected ArrayList<BarModel> m_current_bars_list;
	protected TickerModel m_current_ticker;
	
	public AbsPolicy() {
		// TODO Auto-generated constructor stub
		m_client = new HbdmswapClient();
	}
	
	@Override
	public String getPolicyName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialPolicy(int param_id, ParamManager param_manager) {
		// TODO Auto-generated method stub
		m_param_id = param_id;
		m_param_manger = param_manager;
	}

	@Override
	public void startPolicy() {
		// TODO Auto-generated method stub

	}
	
	protected void updateParam() {
		if(null != m_current_param)
			m_current_param = null;
		
		m_current_param = new BitconParamModel();
		m_current_param = m_param_manger.getPolicyParams(m_param_id);
		System.out.println("AbsPolicy::updateParam::p_id= " + m_param_id + " params= " + m_current_param);
	}
	
	protected void updateBarsList() {
		if(null != m_current_bars_list) {
			m_current_bars_list.clear();
			m_current_bars_list = null;
		}
		
		m_current_bars_list = new ArrayList<BarModel>();
		m_current_bars_list = m_client.getSomeKLine(SystemConfig.PERIOD_1D, 1);
		int bar_size = (null == m_current_bars_list) ? -1 : m_current_bars_list.size();
		System.out.println("AbsPolicy::updateBarsList::p_id= " + m_param_id + " size= " + bar_size);
	}
	
	protected void updateTicker() {
		if(null != m_current_ticker)
			m_current_ticker = null;
		m_current_ticker = new TickerModel();
		m_current_ticker = m_client.getTickerInfo();
		System.out.println("AbsPolicy::updateTicker::p_id= " + m_param_id + " end::ticker= " + m_current_ticker);
	}
	
	protected boolean openBuyOrder() {
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		boolean is_success = m_client.openOneOrder(String.valueOf(m_current_ticker.m_ask), "1", SystemConfig.ORDER_OP_BUY, client_id);
		
		if(is_success) {
			m_current_client_id = client_id;
			m_param_manger.updateOrderTick(m_param_id, client_id, m_current_ticker.m_ask, SystemConfig.INPUT_ORDER_OP_BUY, current_ms);
		}
		System.out.println("AbsPolicy::openBuyOrder::p_id= " + m_param_id + " result= " + is_success);
		return is_success;
	}
	
	protected boolean openSellOrder() {
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		boolean is_success = m_client.openOneOrder(String.valueOf(m_current_ticker.m_bid), "1", SystemConfig.ORDER_OP_SELL, client_id);
		if(is_success) {
			m_current_client_id = client_id;
			m_param_manger.updateOrderTick(m_param_id, client_id, m_current_ticker.m_bid, SystemConfig.INPUT_ORDER_OP_SELL, current_ms);
		}
		System.out.println("AbsPolicy::openSellOrder::p_id= " + m_param_id + " result= " + is_success);	
		return is_success;
	}
	
	protected boolean closeOrder() {
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		String current_order_op = (m_current_param.m_order_type == SystemConfig.INPUT_ORDER_OP_BUY) ? SystemConfig.ORDER_OP_SELL : SystemConfig.ORDER_OP_BUY;
		String current_price = (m_current_param.m_order_type == SystemConfig.INPUT_ORDER_OP_BUY) ? String.valueOf(m_current_ticker.m_bid) : String.valueOf(m_current_ticker.m_ask); 
		
		boolean is_success = m_client.closeOneOrder(client_id, "1", current_order_op);
		if(is_success) {
			m_param_manger.updateOrderTick(m_param_id, "-1", -1, -1, m_current_param.m_order_action_dt);
		}
		
		System.out.println("AbsPolicy::openSellOrder::p_id= " + m_param_id + " client_id= " + client_id + " current_order_op= " + current_order_op + " current_price= " + current_price + 
				" m_order_type= " + m_current_param.m_order_type + " result= " + is_success);
		return is_success;
	}
}
