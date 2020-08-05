package com.frt.BitconTrader;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frt.BitconTrader.api.HbdmswapRestApiV1;
import com.frt.BitconTrader.api.IHbdmswapRestApi;
import com.frt.BitconTrader.common.CommonUtils;
import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.model.BarModel;
import com.frt.BitconTrader.model.TickerModel;

public class HbdmswapClient {

	private IHbdmswapRestApi m_get_client, m_post_client;
	
	public HbdmswapClient() {
		m_get_client = new HbdmswapRestApiV1(SystemConfig.WS_BASE_URL);
		m_post_client = new HbdmswapRestApiV1(SystemConfig.WS_BASE_URL, SystemConfig.A_KEY, SystemConfig.S_KEY);
	}
	
	public ArrayList<BarModel> getSomeKLine(String period, int size) {
		try {
			String res = m_get_client.futureMarketHistoryKline(SystemConfig.SYMBOL, period, String.valueOf(size));
			System.out.println("HbdmswapClient::getSomeKLine::res= " + res);
			
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				ArrayList<BarModel> all_bars = getBarsFromJSON(res, SystemConfig.PLATFORM, period);
				return all_bars;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TickerModel getTickerInfo() {
		try {
			String res = m_get_client.futureMarketDetailMerged(SystemConfig.SYMBOL);
			System.out.println("HbdmswapClient::getTickerInfo::res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				TickerModel t_model = getTickInfoFromJSON(res);
				return t_model;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean openOneOrder(String order_price, String contact_count, String order_op, String client_id) {
		boolean is_success = false;
		try {
			String res = m_post_client.futureContractOrder(SystemConfig.SYMBOL, client_id, order_price, contact_count, order_op, "open", "5", "opponent");
			System.out.println("HbdmswapClient::openOneOrder::ORDER_ID= " + client_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("HbdmswapClient::openOneOrder::order_price= " + order_price + " contact_count= " + contact_count + " id= " + client_id + " result= " + is_success);
		return is_success;
	}
	
	public boolean closeOneOrder(String order_price, String contact_count, String order_op) {
		boolean is_success = false;
		try {
			String client_id = String.valueOf(System.currentTimeMillis());
			String res = m_post_client.futureContractOrder(SystemConfig.SYMBOL, client_id, order_price, contact_count, order_op, "close", "1", "opponent");
			System.out.println("HbdmswapClient::closeOneOrder::ORDER_ID= " + client_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("HbdmswapClient::closeOneOrder::order_price= " + order_price + " contact_count= " + contact_count + " result= " + is_success);
		return is_success;
	}
	
	private TickerModel getTickInfoFromJSON(@Nonnull String input_tick_json) {
		try {
			JSONObject ticker_info_obj = JSONObject.parseObject(input_tick_json);
			JSONObject ticker_obj = ticker_info_obj.getJSONObject("tick");
			
			String all_bid_str = ticker_obj.getString("bid");
			String all_ask_str = ticker_obj.getString("ask");
			
			all_bid_str = all_bid_str.substring(1, (all_bid_str.length() - 1));
			all_ask_str = all_ask_str.substring(1, (all_ask_str.length() - 1));
			
			String bid_str = (all_bid_str.split(","))[0];
			String ask_str = (all_ask_str.split(","))[0];
			
			TickerModel t_model = new TickerModel();
			t_model.m_ask = CommonUtils.formatDecimal(Double.valueOf(ask_str));
			t_model.m_bid = CommonUtils.formatDecimal(Double.valueOf(bid_str));
			t_model.m_high = CommonUtils.formatDecimal(ticker_obj.getDoubleValue("high"));
			t_model.m_open = CommonUtils.formatDecimal(ticker_obj.getDoubleValue("open"));
			t_model.m_low = CommonUtils.formatDecimal(ticker_obj.getDoubleValue("low"));
			t_model.m_close = CommonUtils.formatDecimal(ticker_obj.getDoubleValue("close"));
			t_model.m_id  = ticker_obj.getIntValue("id");
			System.out.println("HbdmswapClient::getTickInfoFromJSON::ticker= " + t_model);
			return t_model;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::getTickInfoFromJSON::JSON ANA FALSE==============");
		}
		return null;
	}
	
	private boolean isSuccess(@Nonnull String result_json) {
		boolean is_success = false;
		try {
			JSONObject all_obj = JSONObject.parseObject(result_json);
			String staus = all_obj.getString("status");
			if("ok".equalsIgnoreCase(staus)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::isSuccess::JSON ANA FALSE==============");
		}
		System.out.println("HbdmswapClient::isSuccess::json= " + result_json + " result= " + is_success);
		return is_success;
	}
	
	private ArrayList<BarModel> getBarsFromJSON(@Nonnull String input_bar_json, String input_partform, String input_cycle) {
		ArrayList<BarModel> all_bars = new ArrayList<BarModel>();
		try {
			JSONObject j_object = JSONObject.parseObject(input_bar_json);
			JSONArray k_data_object = j_object.getJSONArray("data");
			if(null != k_data_object && k_data_object.size() > 0) {
				
				for(int data_index = 0; data_index < k_data_object.size(); data_index ++) {
					JSONObject one_bar_json = k_data_object.getJSONObject(data_index);
					BarModel current_bar = new BarModel();
					current_bar.m_high   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("high"));
					current_bar.m_open   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("open"));
					current_bar.m_low   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("low"));
					current_bar.m_close   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("close"));
					current_bar.m_dt    = System.currentTimeMillis();
					current_bar.m_platform = input_partform;
					current_bar.m_cycle = input_cycle;
					all_bars.add(current_bar);
					System.out.println("HbdmswapClient::getBarFromeJSON::one_bar= " + current_bar);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::isSuccess::json= " + input_bar_json + " p= " + input_partform + " cycle= " + input_cycle + " error=================");
		}
		
		int bars_count = (null == all_bars) ? -1 : all_bars.size();
		System.out.println("HbdmswapClient::isSuccess::json= " + input_bar_json + " p= " + input_partform + " cycle= " + input_cycle + " count= " + bars_count);
		return all_bars;
	}
}
