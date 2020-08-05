package com.frt.BitconTrader.model;

public final class BitconParamModel {
	public int    m_id;
	public String m_policy_name;
	public double m_diff;
	public double m_stop_loss, m_take_profit;
	public String m_order_tick;
	public double m_order_price;
	public int    m_order_type;
	public long   m_order_action_dt;
	
	@Override
	public String toString() {
		return "BitconParamModel [m_id=" + m_id + ", m_policy_name=" + m_policy_name + ", m_diff=" + m_diff
				+ ", m_stop_loss=" + m_stop_loss + ", m_take_profit=" + m_take_profit + ", m_order_tick=" + m_order_tick
				+ ", m_order_price=" + m_order_price + ", m_order_type=" + m_order_type + ", m_order_action_dt=" + m_order_action_dt
				+ "]";
	}
	
	
}
