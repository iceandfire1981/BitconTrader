package com.frt.BitconTrader.model;


public final class TriggerOrderInfo {
	public String m_symbol, m_contract_code, m_trigger_type, m_volume, m_direction, m_offset, m_order_id_str, m_trigger_price, m_order_price, m_order_price_type;
	public int m_order_type, m_lever_rate, m_status;
	public long m_order_id, m_created_at;
	
	@Override
	public String toString() {
		return  m_symbol + "|" + m_contract_code + "|" + m_trigger_type + "|" + m_volume + "|" + m_direction + "|" + m_offset
				+ "|" + m_order_id_str + "|" + m_trigger_price + "|" + m_order_price + "|" + m_order_price_type + "|" + m_order_type
				+ "|" + m_lever_rate + "|" + m_status + "|" + m_order_id + "|" + m_created_at + "\n";
	}
	
}
