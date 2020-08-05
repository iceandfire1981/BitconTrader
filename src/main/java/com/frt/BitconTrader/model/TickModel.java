package com.frt.BitconTrader.model;

public final class TickModel {
	public int m_id;
	public double m_bid, m_ask;
	public long m_dt;
	public String m_platform;
	
	
	@Override
	public String toString() {
		return "TickModel [m_id=" + m_id + ", m_bid=" + m_bid + ", m_ask=" + m_ask + ", m_dt=" + m_dt + ", m_platform="
				+ m_platform + "]";
	}
	
	

}
