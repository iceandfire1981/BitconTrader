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
import com.frt.BitconTrader.model.OrderModel;
import com.frt.BitconTrader.model.TickerModel;
import com.frt.BitconTrader.model.TriggerOrderInfo;

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
	
	public OrderModel getOrderInfo(String client_id) {
		
		try {
			String res = m_post_client.futureContractOrderInfo("", client_id, SystemConfig.SYMBOL, "");
			System.out.println("HbdmswapClient::getOrderInfo::client_id= " + client_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				OrderModel o_model = getOrderInfoFromJson(res);
				return o_model;
			} else {
				System.out.println("HbdmswapClient::getOrderInfo::client_id= " + client_id + " not success=================");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::getOrderInfo::client_id= " + client_id + " exception=================");
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
	
	public boolean openOneOrderLimit(String order_price, String contact_count, String order_op, String client_id) {
		boolean is_success = false;
		try {
			String res = m_post_client.futureContractOrder(SystemConfig.SYMBOL, client_id, order_price, contact_count, order_op, "open", "5", "limit");
			System.out.println("HbdmswapClient::openOneOrderLimit::ORDER_ID= " + client_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("HbdmswapClient::openOneOrderLimit::order_price= " + order_price + " contact_count= " + contact_count + " id= " + client_id + " result= " + is_success);
		return is_success;
		
	}
	
	public int openTriggerOrder(String symbol, String trigger_type, String trigger_price, String order_price, 
			String volume, String direction, String offset, String lever_rate) {
		int  order_id = SystemConfig.INVALID_ORDER_ID;
		
		try {
			String res = m_post_client.futureContractTriggerOrder(symbol, trigger_type, trigger_price, order_price, volume, direction, offset, lever_rate);
			System.out.println("HbdmswapClient::openTriggerOrder::symbol= " + symbol + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				JSONObject data_obj = getDataObj(res);
				order_id = data_obj.getIntValue("order_id");
			} else {
				System.out.println("HbdmswapClient::openTriggerOrder::symbol= " + symbol + " valid request or null res======================");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::openTriggerOrder::symbol= " + symbol + " exception======================");
		}
		System.out.println("HbdmswapClient::openTriggerOrder::trigger_type= " + trigger_type + " trigger_price= " + trigger_price + " direction= " + direction + " order_id= " + order_id);
		return order_id;
	}
	
	public boolean cancelOneTriggerOrder(long order_id) {
		boolean is_success = false;
		try {
			String res = m_post_client.futureCancelTriggerOrder(SystemConfig.SYMBOL, String.valueOf(order_id));
			System.out.println("HbdmswapClient::cancelOneTriggerOrder::ORDER_ID= " + order_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("HbdmswapClient::cancelOneTriggerOrder::order_id= " + order_id + " is_success= " + is_success);
		return is_success;
	}
	
	public boolean openTPSLOrder(String order_price, String contact_count, String order_op, String client_id) {
		boolean is_success = false;
		try {
			String res = m_post_client.futureContractOrder(SystemConfig.SYMBOL, client_id, order_price, contact_count, order_op, "close", "5", "limit");
			System.out.println("HbdmswapClient::openTPSLOrder::ORDER_ID= " + client_id + "   res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				is_success = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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
	
	public void cancelOneOrder(String client_id) {
		System.out.println("HbdmswapClient::closeOneOrder::c_id= " + client_id);
		try {
			String res = m_post_client.futureContractCancel("", client_id, SystemConfig.SYMBOL);
			System.out.println("HbdmswapClient::closeOneOrder::cancelOneOrder::res= " + res);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public ArrayList<TriggerOrderInfo> getTriggerOrders(String contract_code, String page_index, String page_size){
		System.out.println("HbdmswapClient::getTriggerOrders::contract_code= " + contract_code + " page_index= " + page_index + " page_size= " + page_size);
		try {
			String res = m_post_client.futureGetTriggerOrders(contract_code, page_index, page_size);
			System.out.println("HbdmswapClient::getTriggerOrders::res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				ArrayList<TriggerOrderInfo> all_trigger_orders = getTriggerOrdersFromJson(res);
				int orders_size = (null == all_trigger_orders) ? -1 : all_trigger_orders.size();
				System.out.println("HbdmswapClient::getTriggerOrders::contract_code= " + contract_code + " page_index= " + page_index + " page_size= " + page_size + " result size= " + orders_size);
				return all_trigger_orders;
			} else {
				System.out.println("HbdmswapClient::getTriggerOrders::contract_code= " + contract_code + " page_index= " + page_index + " page_size= " + page_size + " result false=========");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<TriggerOrderInfo> getHistoryTriggerOrders(String contract_code, String trade_type, String status,
			String create_date){
		System.out.println("HbdmswapClient::getHistoryTriggerOrders::contract_code= " + contract_code + " trade_type= " + trade_type + " status= " + status + " create_date= " + create_date);
		try {
			String res = m_post_client.futureGetHistoryTriggerOrders(contract_code, trade_type, status, create_date);
			System.out.println("HbdmswapClient::getHistoryTriggerOrders::res= " + res);
			if(!StringUtils.isEmpty(res) && isSuccess(res)) {
				ArrayList<TriggerOrderInfo> all_trigger_orders = getTriggerOrdersFromJson(res);
				int orders_size = (null == all_trigger_orders) ? -1 : all_trigger_orders.size();
				System.out.println("HbdmswapClient::getHistoryTriggerOrders::contract_code= " + contract_code + " result size= " + orders_size);
				return all_trigger_orders;
			} else {
				System.out.println("HbdmswapClient::getHistoryTriggerOrders::contract_code= " + contract_code + " result false=========");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	
	
	private JSONObject getDataObj(@Nonnull String result_json) {
		JSONObject data_json_obj = null;
		try {
			JSONObject all_obj = JSONObject.parseObject(result_json);
			if(null != all_obj && all_obj.containsKey("data")) {
				data_json_obj = all_obj.getJSONObject("data");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::getDataObj::JSON ANA FALSE==============");
		}
		System.out.println("HbdmswapClient::getDataObj::data_json_obj= " + data_json_obj);
		return data_json_obj;
	}
	
	private ArrayList<BarModel> getBarsFromJSON(@Nonnull String input_bar_json, String input_partform, String input_cycle) {
		ArrayList<BarModel> all_bars = new ArrayList<BarModel>();
		try {
			JSONObject j_object = JSONObject.parseObject(input_bar_json);
			long bar_ts = j_object.getLongValue("ts");
			JSONArray k_data_object = j_object.getJSONArray("data");
			if(null != k_data_object && k_data_object.size() > 0) {
				
				for(int data_index = 0; data_index < k_data_object.size(); data_index ++) {
					JSONObject one_bar_json = k_data_object.getJSONObject(data_index);
					BarModel current_bar = new BarModel();
					current_bar.m_high   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("high"));
					current_bar.m_open   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("open"));
					current_bar.m_low   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("low"));
					current_bar.m_close   = CommonUtils.formatDecimal(one_bar_json.getDoubleValue("close"));
					current_bar.m_dt    = bar_ts;
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
	
	private OrderModel getOrderInfoFromJson(@Nonnull String input_json) {
		try {
			JSONObject j_obj = JSONObject.parseObject(input_json);
			JSONArray data_list = j_obj.getJSONArray("data");
			if (null != data_list && data_list.size() > 0) {
				JSONObject one_data = (JSONObject) data_list.get(0);
				OrderModel o_model = new OrderModel();
				o_model.m_client_order_id = one_data.getString("client_order_id");
				o_model.m_contract_code = one_data.getString("contract_code");
				o_model.m_created_at = one_data.getLong("created_at");
				o_model.m_direction = one_data.getString("direction");
				o_model.m_offset = one_data.getString("offset");
				o_model.m_order_id = one_data.getString("order_id");
				o_model.m_order_source = one_data.getString("order_source");
				o_model.m_price = one_data.getDouble("price");
				o_model.m_status = one_data.getInteger("status");
				System.out.println("HbdmswapClient::getOrderInfoFromJson::OrderModel= " + o_model);
				return o_model;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HbdmswapClient::isSuccess::json= " + input_json + " error=================");
		}
		return null;
	}
	
	private ArrayList<TriggerOrderInfo> getTriggerOrdersFromJson(String input_result){
		try {
			JSONObject all_obj = JSONObject.parseObject(input_result);
			JSONObject data_obj = all_obj.getJSONObject("data");
			JSONArray  orders_objs = data_obj.getJSONArray("orders");
			if(null != orders_objs && orders_objs.size() > 0) {
				ArrayList<TriggerOrderInfo> all_orders = new ArrayList<TriggerOrderInfo>();
				for(int order_index = 0; order_index < orders_objs.size(); order_index ++) {
					JSONObject current_order_json = orders_objs.getJSONObject(order_index);
					TriggerOrderInfo current_order = new TriggerOrderInfo();
					current_order.m_symbol = current_order_json.getString("symbol");
					current_order.m_contract_code = current_order_json.getString("contract_code");
					current_order.m_trigger_type = current_order_json.getString("trigger_type");
					current_order.m_volume = current_order_json.getString("volume");
					current_order.m_order_type = current_order_json.getIntValue("order_type");
					current_order.m_direction = current_order_json.getString("direction");
					current_order.m_offset = current_order_json.getString("offset");
					current_order.m_lever_rate = current_order_json.getInteger("lever_rate");
					current_order.m_order_id = current_order_json.getLongValue("order_id");
					current_order.m_order_id_str = current_order_json.getString("order_id_str");
					current_order.m_trigger_price = current_order_json.getString("trigger_price");
					current_order.m_order_price = current_order_json.getString("order_price");
					current_order.m_created_at = current_order_json.getLongValue("created_at");
					current_order.m_order_price_type = current_order_json.getString("order_price_type");
					current_order.m_status = current_order_json.getIntValue("status");
					all_orders.add(current_order);
				}
				return all_orders;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
}
