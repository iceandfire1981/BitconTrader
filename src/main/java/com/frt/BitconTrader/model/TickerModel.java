package com.frt.BitconTrader.model;

public class TickerModel {
	public double m_bid, m_ask, m_high, m_open, m_low, m_close;
	public int    m_id;
	
	@Override
	public String toString() {
		return "TickerModel [m_bid=" + m_bid + ", m_ask=" + m_ask + ", m_high=" + m_high + ", m_open=" + m_open
				+ ", m_low=" + m_low + ", m_close=" + m_close + ", m_id=" + m_id + "]";
	}
	
}
