package com.frt.BitconTrader.model;

public class OrderModel {

	public static final int ORDER_ALL_DUE = 6;//订单全部成交 
	
	public String m_client_order_id;
	public String m_contract_code;
	public long m_created_at;
	public String m_direction;
	public String m_offset;
	public String m_order_id;
	public String m_order_source;
	public double m_price;
	public int    m_status;

	@Override
	public String toString() {
		return  m_client_order_id + "|" + m_contract_code + "|" + m_created_at + "|" + m_direction + "|" + m_offset
				+ "|" + m_order_id + "|" + m_order_source + "|" + m_price
				+ "|" + m_status + "\n";
	}

}
