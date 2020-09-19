package com.frt.BitconTrader.model;

import com.frt.BitconTrader.common.CommonUtils;

public class TradeInfoModel {

	public double m_high, m_low, m_diff;
	
	public static final int DIFF_NOT_CHANGE = 0;
	public static final int DIFF_HIGH_CHANGE = DIFF_NOT_CHANGE + 1;
	public static final int DIFF_LOW_CHANGE = DIFF_NOT_CHANGE + 2;
	
	public TradeInfoModel(double c_high,double c_low) {
		m_high = CommonUtils.formatDecimal(c_high);
		m_low  = CommonUtils.formatDecimal(c_low);
		m_diff = CommonUtils.formatDecimal(m_high - m_low);
	}
	
	public int updateDiff(double c_high, double c_low) {
		int had_udpate = DIFF_NOT_CHANGE;
		double temp_high = CommonUtils.formatDecimal(c_high);
		double temp_low  = CommonUtils.formatDecimal(c_low);
		double temp_diff = CommonUtils.formatDecimal(temp_high - temp_low);
		
		if(temp_diff > m_diff) {
			System.out.println("TradeInfoModel::updateDiff::m_high= " + m_high + " m_low= " + m_low + " m_diff= " + m_diff);
			System.out.println("TradeInfoModel::updateDiff::temp_high= " + temp_high + " temp_low= " + temp_low + " temp_diff= " + temp_diff);
			if(m_high < temp_high) {
				System.out.println("TradeInfoModel::updateDiff::Diff Has change, upper changed=========================");
				m_high = temp_high;
				m_low  = temp_low;
				m_diff = temp_diff;
				had_udpate = DIFF_HIGH_CHANGE;
			} else if(m_low > temp_low) {
				System.out.println("TradeInfoModel::updateDiff::Diff Has change, lower changed=========================");
				m_high = temp_high;
				m_low  = temp_low;
				m_diff = temp_diff;
				had_udpate = DIFF_LOW_CHANGE;
			} else {
				System.out.println("TradeInfoModel::updateDiff::Diff Has change but upper and lower had not change=========================");
				m_high = temp_high;
				m_low  = temp_low;
				m_diff = temp_diff;
			}
			
			
		} else {
			had_udpate = DIFF_NOT_CHANGE;
			System.out.println("TradeInfoModel::updateDiff::Diff not change=========================");
		}
		
		return had_udpate;
	}
	
	@Override
	public String toString() {
		return "TradeInfoModel [m_high=" + m_high + ", m_low=" + m_low + ", m_diff=" + m_diff + "]";
	}
	
	
}
