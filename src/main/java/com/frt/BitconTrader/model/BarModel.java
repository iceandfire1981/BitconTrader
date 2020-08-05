package com.frt.BitconTrader.model;

public final class BarModel {
	public int m_id;
	public long m_dt;
	public double m_high, m_open, m_low, m_close;
	public String m_platform, m_cycle;
	
	@Override
	public String toString() {
		return "BarModel [m_id=" + m_id + ", m_dt=" + m_dt + ", m_high=" + m_high + ", m_open=" + m_open + ", m_low="
				+ m_low + ", m_close=" + m_close + ", m_platform=" + m_platform + ", m_cycle=" + m_cycle + "]";
	}
	
	

}
