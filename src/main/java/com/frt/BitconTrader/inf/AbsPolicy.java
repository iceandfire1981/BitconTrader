package com.frt.BitconTrader.inf;

import java.util.ArrayList;

import com.frt.BitconTrader.HbdmswapClient;
import com.frt.BitconTrader.common.CommonUtils;
import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.manager.ParamManager;
import com.frt.BitconTrader.model.BarModel;
import com.frt.BitconTrader.model.BitconParamModel;
import com.frt.BitconTrader.model.OrderModel;
import com.frt.BitconTrader.model.TickerModel;
import com.frt.BitconTrader.model.TradeInfoModel;
import com.frt.BitconTrader.model.TriggerOrderInfo;

public class AbsPolicy implements PolicyInterface {

	protected int m_param_id;
	protected String m_current_client_id;
	protected ParamManager m_param_manger;
	protected HbdmswapClient m_client;
	protected BitconParamModel m_current_param;
	protected ArrayList<BarModel> m_current_bars_list;
	protected TickerModel m_current_ticker;
	protected TradeInfoModel m_trade_model;
	
	protected int m_diff_had_change;
	protected long m_pre_buy_client_id, m_pre_sell_client_id;
	protected long m_tp_order_id, m_sl_order_id;
	protected long m_create_dt;
	
	protected boolean need_write = true;
	
	public AbsPolicy() {
		// TODO Auto-generated constructor stub
		m_client = new HbdmswapClient();
		m_trade_model = new TradeInfoModel(0d, 0d);
		m_diff_had_change = TradeInfoModel.DIFF_NOT_CHANGE;
		
		m_pre_buy_client_id = -1l;
		m_pre_sell_client_id = -1l;
		
		m_tp_order_id = -1;
		m_sl_order_id = -1;
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
		System.out.println("AbsPolicy::updateParam::id= " + m_param_id + " params= " + m_current_param);
	}
	
	/**
	 * Create all pre-order, one buy pre-order, one sell pre-order
	 * @param pre_buy_price
	 * @param pre_sell_price
	 * @return
	 */
	protected boolean createAllPreOrder(double pre_buy_price, double pre_sell_price) {
		System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + " pre_buy_price= " + pre_buy_price + 
				" pre_sell_price= " + pre_sell_price + " into create current pre order====================");
		boolean create_buy_order_result = false;
		boolean create_sell_order_result = false;
		long buy_pre_order_client_id = System.currentTimeMillis();
		long sell_pre_order_client_id = -1l;
		
		String buy_pre_order_client_id_str = String.valueOf(buy_pre_order_client_id);
		create_buy_order_result = openBuyOrderLimited(pre_buy_price, buy_pre_order_client_id_str);
		System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + " pre_buy_price= " + pre_buy_price + " Create buy pre-order result is[" + create_buy_order_result + "]");
		
		if(create_buy_order_result) {
			sell_pre_order_client_id = System.currentTimeMillis();
			String sell_pre_order_client_id_str = String.valueOf(sell_pre_order_client_id);
			create_sell_order_result = openSellOrderLimited(pre_sell_price, sell_pre_order_client_id_str);
			System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + " pre_sell_price= " + pre_sell_price + " Create sell pre-order result is[" + create_sell_order_result + "]");
			if (!create_sell_order_result) {
				System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + "create sell pre-order false, the buy pre-order will cancel============================");
				cancelOrder(buy_pre_order_client_id_str);
			}
		}
		
		if(create_buy_order_result && create_sell_order_result) {
			m_pre_buy_client_id = buy_pre_order_client_id;
			m_pre_sell_client_id = sell_pre_order_client_id;
			System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + "create all pre-order success, b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id); 
			return true;
		} else {
			m_pre_buy_client_id = -1l;
			m_pre_sell_client_id = -1l;
			System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + "create all pre-order false, b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id); 
			return false;
		}
		
	}
	
	protected void updateOrderState() {
		System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id + " watch pre-order status");
		
		if(m_pre_buy_client_id > 0l && m_pre_sell_client_id > 0l) {//目前处于挂单状态, 监视挂单成交状态, 如果一方成交, 挂止盈和止损单子, 以及删除另一个挂单
			System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + ", b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id + " get order info");
			OrderModel pre_buy_order = m_client.getOrderInfo(String.valueOf(m_pre_buy_client_id));
			OrderModel pre_sell_order = m_client.getOrderInfo(String.valueOf(m_pre_sell_client_id));
			
			if(null == pre_buy_order || null == pre_sell_order) {
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_order= " + pre_buy_order + " s_order= " + pre_sell_order + " get info error=============");
				return;
			}
			System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_order= " + pre_buy_order);
			System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", s_order= " + pre_sell_order);
			
			if(pre_buy_order.m_status == OrderModel.ORDER_ALL_DUE) {//如果买方挂单已经成交. 清理买方挂单, 并挂止盈止损单
				m_param_manger.updateOrderTick(m_param_id, pre_buy_order.m_client_order_id, pre_buy_order.m_price, SystemConfig.INPUT_ORDER_OP_BUY, pre_buy_order.m_created_at);
				m_client.cancelOneOrder(String.valueOf(m_pre_sell_client_id));
				m_pre_buy_client_id = -1l;
				m_pre_sell_client_id = -1l;
				
				if(need_write) {
					System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_order= " + pre_buy_order + " had due write record===============");
					CommonUtils.writeTradeLog(pre_buy_order.toString());
					need_write = false;
				}
				
				double tp_price = CommonUtils.formatDecimal(pre_buy_order.m_price + m_current_param.m_take_profit);//take profit price;
				double sl_price = CommonUtils.formatDecimal(pre_buy_order.m_price - m_current_param.m_stop_loss);//take profit price;
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_order= " + pre_buy_order + " had due===============");
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", b_order::tp_price= " + tp_price + " sl_price= " + sl_price + " t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id);
				handleTpSlOrder();
				return;
			}
			
			if(pre_sell_order.m_status == OrderModel.ORDER_ALL_DUE) {//如果卖方挂单已经成交. 清理买方挂单, 并挂止盈止损单
				
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", s_order= " + pre_sell_order + " had due ===============");
				m_param_manger.updateOrderTick(m_param_id, pre_sell_order.m_client_order_id, pre_sell_order.m_price, SystemConfig.INPUT_ORDER_OP_SELL, pre_sell_order.m_created_at);
				m_client.cancelOneOrder(String.valueOf(m_pre_buy_client_id));
				m_pre_buy_client_id = -1l;
				m_pre_sell_client_id = -1l;
				
				if(need_write) {
					System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", s_order= " + pre_sell_order + " had due write record===============");
					CommonUtils.writeTradeLog(pre_sell_order.toString());
					need_write = false;
				}
				
				double tp_price = CommonUtils.formatDecimal(pre_sell_order.m_price - m_current_param.m_take_profit);//take profit price;
				double sl_price = CommonUtils.formatDecimal(pre_sell_order.m_price + m_current_param.m_stop_loss);//take profit price;
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", s_order= " + pre_sell_order + " had due");
				System.out.println("AbsPolicy::updateOrderState::id= " + m_param_id + ", s_order::tp_price= " + tp_price + " sl_price= " + sl_price + " t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id);
				handleTpSlOrder();
				return;
			}
		} else {
			System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + ", b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id + " pre_order NOT HERE==========");
			if(m_pre_buy_client_id > 0l) {
				System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + ", b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id + " buy_pre_order cancel==========");
				m_client.cancelOneOrder(String.valueOf(m_pre_buy_client_id));
			}
			
			if(m_pre_sell_client_id > 0l) {
				System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + ", b_id= " + m_pre_buy_client_id + " s_id= " + m_pre_sell_client_id + " sell_pre_order cancel==========");
				m_client.cancelOneOrder(String.valueOf(m_pre_sell_client_id));
			}
			
			m_pre_buy_client_id = -1l;
			m_pre_sell_client_id = -1l;
			return;
		}
		
	}
	
	protected boolean handleTpSlOrder() {
		updateParam();
		
		if(!SystemConfig.INVALID_CLIENT_ID.equalsIgnoreCase(m_current_param.m_order_tick)) {
			ArrayList<TriggerOrderInfo> all_h_t_orders = m_client.getHistoryTriggerOrders(SystemConfig.SYMBOL, "0", "0", "1");
			int h_size = (null == all_h_t_orders) ? 0:all_h_t_orders.size();
			System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " h_size= " + h_size);
			if(h_size > 0) {
				for(TriggerOrderInfo current_order : all_h_t_orders) {
					long c_order_id =  current_order.m_order_id;
					System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " order_id= " + c_order_id);
					
					if(m_tp_order_id == c_order_id) {
						System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " tp order had dued==========");
						m_client.cancelOneTriggerOrder(m_sl_order_id);
						
						m_tp_order_id = -1l;
						m_sl_order_id = -1l;
						m_pre_buy_client_id = -1l;
						m_pre_sell_client_id = -1l;
						m_param_manger.updateOrderTick(m_param_id, "-1", -1, -1, m_current_param.m_order_action_dt);
						CommonUtils.writeTradeLog(current_order.toString());
						return true;
					}
					
					if(m_sl_order_id == c_order_id) {
						System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " sl order had dued==========");
						m_client.cancelOneTriggerOrder(m_tp_order_id);
						
						m_tp_order_id = -1l;
						m_sl_order_id = -1l;
						m_pre_buy_client_id = -1l;
						m_pre_sell_client_id = -1l;
						m_param_manger.updateOrderTick(m_param_id, "-1", -1, -1, m_current_param.m_order_action_dt);
						CommonUtils.writeTradeLog(current_order.toString());
						return true;
					}
				}
			}
			
			if(m_tp_order_id != SystemConfig.INVALID_ORDER_ID && m_sl_order_id != SystemConfig.INVALID_ORDER_ID) {// stop loss and take profit order create success
				System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " h_size= " + h_size + " all tp/sl orders had ready. wait due======");
				return false;
			}
			
			OrderModel due_order = m_client.getOrderInfo(m_current_param.m_order_tick);
			String due_order_op = due_order.m_direction;
			
			double tp_price = -1d;
			double sl_price = -1d;
			String tp_trigger_type = "";
			String sl_trigger_type = "";
			String order_op = "";
			
			double order_price = CommonUtils.formatDecimal(due_order.m_price);
			
			if(SystemConfig.ORDER_OP_BUY.equalsIgnoreCase(due_order_op)) {// Is BUY order had due
				tp_price = CommonUtils.formatDecimal(order_price + m_current_param.m_take_profit);//take profit price;
				sl_price = CommonUtils.formatDecimal(order_price - m_current_param.m_stop_loss);//take profit price;
				order_op = SystemConfig.ORDER_OP_SELL;
				tp_trigger_type = SystemConfig.TRIGGER_TYPE_GE;
				sl_trigger_type = SystemConfig.TRIGGER_TYPE_LE;
			} else if(SystemConfig.ORDER_OP_SELL.equalsIgnoreCase(due_order_op)) {// Is SELL order had due
				tp_price = CommonUtils.formatDecimal(order_price - m_current_param.m_take_profit);//take profit price;
				sl_price = CommonUtils.formatDecimal(order_price + m_current_param.m_stop_loss);//take profit price;
				order_op = SystemConfig.ORDER_OP_BUY;
				tp_trigger_type = SystemConfig.TRIGGER_TYPE_LE;
				sl_trigger_type = SystemConfig.TRIGGER_TYPE_GE;
			} else {
				System.out.println("AbsPolicy::createAllPreOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " invaild order operation=============");
				return false;
			}
			
			System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + " tp_price= " + tp_price + " sl_price= " + sl_price + " op= " + order_op + 
					" order_price= " + order_price + " tp_trigger_type= " + tp_trigger_type + " sl_trigger_type= " + sl_trigger_type);
			
			if(m_tp_order_id == SystemConfig.INVALID_ORDER_ID) {
				int temp_tp_order_id = m_client.openTriggerOrder(SystemConfig.SYMBOL, tp_trigger_type, String.valueOf(tp_price), String.valueOf(tp_price), "1", order_op, "close", "5");
				System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " have no tp_order create, result= " + temp_tp_order_id);
				if(temp_tp_order_id != SystemConfig.INVALID_ORDER_ID) {
					m_tp_order_id = temp_tp_order_id;
				}
			} else {
				System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " tp_order had created skip=============");
			}
			
			if(m_sl_order_id == SystemConfig.INVALID_ORDER_ID) {
				int temp_sl_order_id = m_client.openTriggerOrder(SystemConfig.SYMBOL, sl_trigger_type, String.valueOf(sl_price), String.valueOf(sl_price), "1", order_op, "close", "5");
				System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " have no sl_order create, result= " + temp_sl_order_id);
				if(temp_sl_order_id != SystemConfig.INVALID_ORDER_ID) {
					m_sl_order_id = temp_sl_order_id;
				}
			} else {
				System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " sl_order had created skip=============");
			}
		} else {
			System.out.println("AbsPolicy::handleTpSlOrder::id= " + m_param_id + ", t_id= " + m_tp_order_id + " s_id= " + m_sl_order_id + " have no due order=============");
		}
		
		return false;
	}
	
	protected void updateBarsList() {
		if(null != m_current_bars_list) {
			m_current_bars_list.clear();
			m_current_bars_list = null;
		}
		
		m_current_bars_list = new ArrayList<BarModel>();
		m_current_bars_list = m_client.getSomeKLine(SystemConfig.PERIOD_1D, 1);
		int bar_size = (null == m_current_bars_list) ? -1 : m_current_bars_list.size();
		
		if(bar_size > 0) {
			m_diff_had_change = m_trade_model.updateDiff(m_current_bars_list.get(0).m_high, m_current_bars_list.get(0).m_low);
		}
		
		System.out.println("AbsPolicy::updateBarsList::id= " + m_param_id + " size= " + bar_size + " trade_info= " + m_trade_model + " change= " + m_diff_had_change);
	}
	
	protected void updateTicker() {
		if(null != m_current_ticker)
			m_current_ticker = null;
		m_current_ticker = new TickerModel();
		m_current_ticker = m_client.getTickerInfo();
		System.out.println("AbsPolicy::updateTicker::id= " + m_param_id + " end::ticker= " + m_current_ticker);
	}
	
	protected boolean openBuyOrder() {
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		boolean is_success = m_client.openOneOrder(String.valueOf(m_current_ticker.m_ask), "1", SystemConfig.ORDER_OP_BUY, client_id);
		
		if(is_success) {
			OrderModel o_model = m_client.getOrderInfo(client_id);
			System.out.println("AbsPolicy::openBuyOrder::id= " + m_param_id + " o_model= " + o_model);
			if(OrderModel.ORDER_ALL_DUE != o_model.m_status) {
				m_client.cancelOneOrder(client_id);
			} else {
				m_current_client_id = client_id;
				m_param_manger.updateOrderTick(m_param_id, client_id, m_current_ticker.m_ask,
						SystemConfig.INPUT_ORDER_OP_BUY, current_ms);
			}
		}
		System.out.println("AbsPolicy::openBuyOrder::id= " + m_param_id + " result= " + is_success);
		return is_success;
	}
	
	protected boolean openBuyOrderLimited(double buy_price, String client_id) {
		boolean is_success = m_client.openOneOrderLimit(String.valueOf(buy_price), "1", SystemConfig.ORDER_OP_BUY, client_id);
		OrderModel o_model = m_client.getOrderInfo(client_id);
		System.out.println("AbsPolicy::openBuyOrderLimited::id= " + m_param_id + " result= " + is_success + " o_info= " + o_model + " c_id= " + client_id);
		return is_success;
	}
	
	protected boolean openSellOrder() {
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		boolean is_success = m_client.openOneOrder(String.valueOf(m_current_ticker.m_bid), "1", SystemConfig.ORDER_OP_SELL, client_id);
		if(is_success) {
			OrderModel o_model = m_client.getOrderInfo(client_id);
			System.out.println("AbsPolicy::openSellOrder::id= " + m_param_id + " o_model= " + o_model);
			
			if(OrderModel.ORDER_ALL_DUE != o_model.m_status) {
				m_client.cancelOneOrder(client_id);
			} else {
				m_current_client_id = client_id;
				m_param_manger.updateOrderTick(m_param_id, client_id, m_current_ticker.m_bid, SystemConfig.INPUT_ORDER_OP_SELL, current_ms);
			}
		}
		System.out.println("AbsPolicy::openSellOrder::id= " + m_param_id + " result= " + is_success);	
		return is_success;
	}
	
	protected boolean openSellOrderLimited(double sell_price, String client_id) {
		boolean is_success = m_client.openOneOrderLimit(String.valueOf(sell_price), "1", SystemConfig.ORDER_OP_SELL, client_id);
		OrderModel o_model = m_client.getOrderInfo(client_id);
		System.out.println("AbsPolicy::openSellOrderLimited::id= " + m_param_id + " result= " + is_success + " o_info= " + o_model + " c_id= " + client_id);
		return is_success;
	}
	
	protected boolean closeOrder() {
//		m_client.cancelOneOrder(String.valueOf(m_pre_buy_client_id));
//		m_client.cancelOneOrder(String.valueOf(m_pre_sell_client_id));
		
		if(SystemConfig.INVALID_ORDER_ID != m_sl_order_id) {
			m_client.cancelOneTriggerOrder(m_sl_order_id);
			m_sl_order_id = SystemConfig.INVALID_ORDER_ID;
		}
		
		if(SystemConfig.INVALID_ORDER_ID != m_tp_order_id) {
			m_client.cancelOneTriggerOrder(m_tp_order_id);
			m_tp_order_id = SystemConfig.INVALID_ORDER_ID;
		}
		
		if(m_pre_buy_client_id > 0l)
			m_client.cancelOneOrder(String.valueOf(m_pre_buy_client_id));
		
		if(m_pre_sell_client_id > 0l)
			m_client.cancelOneOrder(String.valueOf(m_pre_sell_client_id));
		
		m_pre_buy_client_id = -1l;
		m_pre_sell_client_id = -1l;
		
		long current_ms = System.currentTimeMillis();
		String client_id = String.valueOf(current_ms);
		String current_order_op = (m_current_param.m_order_type == SystemConfig.INPUT_ORDER_OP_BUY) ? SystemConfig.ORDER_OP_SELL : SystemConfig.ORDER_OP_BUY;
		String current_price = (m_current_param.m_order_type == SystemConfig.INPUT_ORDER_OP_BUY) ? String.valueOf(m_current_ticker.m_bid) : String.valueOf(m_current_ticker.m_ask); 
		
		boolean is_success = m_client.closeOneOrder(client_id, "1", current_order_op);
		if(is_success) {
//			m_client.cancelOneOrder(String.valueOf(m_pre_buy_client_id));
//			m_client.cancelOneOrder(String.valueOf(m_pre_sell_client_id));
//			m_client.cancelOneOrder(String.valueOf(m_sl_client_id));
//			
//			m_pre_buy_client_id = -1l;
//			m_pre_sell_client_id = -1l;
//			m_sl_client_id = -1;
			m_param_manger.updateOrderTick(m_param_id, "-1", -1, -1, m_current_param.m_order_action_dt);
			String record = "time-out|" + current_price + "\n";
			CommonUtils.writeTradeLog(record);
		}
		
		System.out.println("AbsPolicy::openSellOrder::id= " + m_param_id + " client_id= " + client_id + " current_order_op= " + current_order_op + " current_price= " + current_price + 
				" m_order_type= " + m_current_param.m_order_type + " result= " + is_success);
		return is_success;
	}
	
	protected void cancelOrder(String cilent_id) {
		System.out.println("AbsPolicy::cancelOrder::id= " + m_param_id + " cilent_id= " + cilent_id);
		m_client.cancelOneOrder(cilent_id);
	}
}
