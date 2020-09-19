package com.frt.BitconTrader.policy;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.frt.BitconTrader.common.CommonUtils;
import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.inf.AbsPolicy;
import com.frt.BitconTrader.manager.ParamManager;
import com.frt.BitconTrader.model.TradeInfoModel;

public class BitconPolicyEx extends AbsPolicy {
	private static final String POLICY_NAME = "bitcon_diff_policy_ex";
	private static final int DIFF_GAP = 10;
	
	@Override
	public String getPolicyName() {
		// TODO Auto-generated method stub
		return POLICY_NAME;
	}

	@Override
	public void initialPolicy(int param_id, ParamManager param_manager) {
		// TODO Auto-generated method stub
		super.initialPolicy(param_id, param_manager);
		System.out.println("BitconPolicyEx::initialPolicy::id= " + m_param_id);
	}

	@Override
	public void startPolicy() {
		// TODO Auto-generated method stub
		System.out.println("BitconPolicyEx::startPolicy::id= " + m_param_id + " Thread start begin==================");
		//actionPolicyOnce();
		Thread action_thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					actionPolicyOnce();

					try {
						Thread.sleep(2000);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		});
		action_thread.start();
		System.out.println("BitconPolicyEx::startPolicy::id= " + m_param_id + " Thread start end==================");
	}
	
	private void actionPolicyOnce() {
		System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " Thread start begin==================");
		updateOrderState();
		updateParam();
		updateBarsList();
		updateTicker();
		
		if(null == m_current_param || null == m_current_bars_list || null == m_current_ticker) {
			System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " info::handler invalid market value=============");
			return;
		}
		
		String order_tick = m_current_param.m_order_tick;
		double order_price = CommonUtils.formatDecimal(m_current_param.m_order_price);
		double target_diff = CommonUtils.formatDecimal(m_current_param.m_diff);
		double param_sl = CommonUtils.formatDecimal(m_current_param.m_stop_loss);
		double param_tp = CommonUtils.formatDecimal(m_current_param.m_take_profit);
		
		System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + 
				" order_tick= " + order_tick + " order_price= " + order_price + " target_diff= " + target_diff +  " param_sl= " + param_sl + " param_tp= " + param_tp);
		
		double bid = m_current_ticker.m_bid;
		double ask = m_current_ticker.m_ask;
		
		double high = CommonUtils.formatDecimal(m_current_bars_list.get(0).m_high);
		double open = CommonUtils.formatDecimal(m_current_bars_list.get(0).m_open);
		double low  = CommonUtils.formatDecimal(m_current_bars_list.get(0).m_low);
		double close = CommonUtils.formatDecimal(m_current_bars_list.get(0).m_close);
		double current_diff = Math.abs(CommonUtils.formatDecimal(high-low));
		double current_diff_real = CommonUtils.formatDecimal(close - open);
		
		long order_action_dt = m_current_param.m_order_action_dt;
		long current_dt = m_current_bars_list.get(0).m_dt;//System.currentTimeMillis();
		boolean is_same_day = (order_action_dt <= 0) ? false : CommonUtils.isSameDay(order_action_dt, current_dt);
		
		if(!is_same_day) {
			need_write = true;
		}
		
		
		System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + 
				" bid= " + bid + " ask= " + ask + " c_diff= " + current_diff + " c_diff_r= " + current_diff_real +
				" high= " + high + " open = " + open + " low= " + low + " close= " + close + 
				" o_dt= " + order_action_dt + " c_dt= " + current_dt  +  " is_same_day= " + is_same_day + " o_d_str= " + getDateString(order_action_dt) + " c_dt= " + getDateString(current_dt));
		
		if ("-1".equalsIgnoreCase(order_tick)) {//开仓处理
			System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " info::Handle Open order=============");
			if(is_same_day) {
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::In smae day, do not due, id= " +  m_param_id);
				return;
			}
			
			if(m_pre_buy_client_id <= 0) {//Have no order
				double pre_diff = CommonUtils.formatDecimal(m_current_param.m_diff - DIFF_GAP);
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::No any order here, will create one, c_diff= " + current_diff + " p_diff= " + pre_diff + " p_diff= " + m_current_param.m_diff);
				if(current_diff >= pre_diff) {//Reach to gap, will create pre-order
					double sell_price = low + m_current_param.m_diff;
					double buy_price = high - m_current_param.m_diff;
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::Reach gap diff, will create order::b_p= " + buy_price + " s_p= " + sell_price);
					createAllPreOrder(buy_price, sell_price);
					return;
				} else {
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::Did not reach gap diff");
					return;
				}
			} else {
				double sell_price = low + m_current_param.m_diff;
				double buy_price = high - m_current_param.m_diff;
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::Range had change, will re-create one, c_diff= " + current_diff + " m_diff_had_change= " + m_diff_had_change + " p_diff= " + m_current_param.m_diff);
				if(m_diff_had_change == TradeInfoModel.DIFF_HIGH_CHANGE) {//最高值发生变化
					cancelOrder(String.valueOf(m_pre_buy_client_id));
					m_pre_buy_client_id = -1;
					long buy_client_id = System.currentTimeMillis();
					String buy_client_id_str = String.valueOf(buy_client_id);
					boolean is_buy_success = openBuyOrderLimited(buy_price, buy_client_id_str);
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + "Upper value has changed re-create pre-buy-order, c_id= " + buy_client_id_str + " result= " + is_buy_success);
					
					if(is_buy_success) {
						m_pre_buy_client_id = buy_client_id;
					} else {
						cancelOrder(String.valueOf(m_pre_sell_client_id));
						m_pre_sell_client_id =  -1;
						m_pre_buy_client_id = -1;
					}
					return;
				} else if(m_diff_had_change == TradeInfoModel.DIFF_LOW_CHANGE){
					cancelOrder(String.valueOf(m_pre_sell_client_id));
					m_pre_sell_client_id = -1;
					long sell_client_id = System.currentTimeMillis();
					String sell_client_id_str = String.valueOf(sell_client_id);
					boolean is_sell_success = openSellOrderLimited(sell_price, sell_client_id_str);
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + "Lower value has changed re-create pre-sell-order, c_id= " + sell_client_id_str + " result= " + is_sell_success);
					
					if(is_sell_success)
						m_pre_sell_client_id = sell_client_id;
					else {
						cancelOrder(String.valueOf(m_pre_buy_client_id));
						m_pre_buy_client_id =  -1;
						m_pre_sell_client_id = -1;
					}
					return;
				} else {
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " ::info::Has pre order, but not need to change order setting");
					return;
				}
			}
			
		} else {//关仓监视
			if(!is_same_day) {
				closeOrder();
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::diffrence day will close my order" );
				return;
			} else {
				boolean had_due = handleTpSlOrder();
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::in due process close_sl_flag= " + had_due);
				
				if(had_due) {
					System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::out due process close_sl_flag= " +  had_due + " order has close by stop loss");
					return;
				}
				
				double current_diff_cancel = CommonUtils.formatDecimal((order_price - close));
				int    order_type = m_current_param.m_order_type;
				System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " sell::order_price= " + order_price + " current_diff= " + current_diff_cancel + " order_type= " + order_type);
				
				if(SystemConfig.INPUT_ORDER_OP_BUY == order_type) {
					if(current_diff_cancel >= 0 && Math.abs(current_diff_cancel) >= param_sl){
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::close buy order cause buy order stop loss" );
						closeOrder();
					} else if(current_diff_cancel < 0 && Math.abs(current_diff_cancel) >= param_tp) {
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::close sell order cause buy order take profit" );
						closeOrder();
					} else {
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::have buy order do nothing" );
					}
				} else {
					if(current_diff_cancel >= 0 && Math.abs(current_diff_cancel) >= param_tp){
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::close sell order cause buy order take profit" );
						closeOrder();
					} else if(current_diff_cancel < 0 && Math.abs(current_diff_cancel) >= param_sl) {
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::close sell order cause buy order stop loss" );
						closeOrder();
					} else {
						System.out.println("BitconPolicyEx::actionPolicyOnce::id= " +  m_param_id + " info::have sell order do nothing" );
					}
				}
			}
		}
		
		System.out.println("BitconPolicyEx::actionPolicyOnce::id= " + m_param_id + " Thread start end==================");
	}
	
	private String getDateString(long input_ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = sdf.format(new Date(input_ts));
		return result;
	}
}
