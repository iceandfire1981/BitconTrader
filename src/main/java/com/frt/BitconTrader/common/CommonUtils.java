package com.frt.BitconTrader.common;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frt.BitconTrader.model.BarModel;
import com.frt.BitconTrader.model.TickerModel;

public final class CommonUtils {

	public static final File getParamsDir() {
		String final_dir_path = SystemConfig.BASE_DIR + SystemConfig.PARAM_CONFIG_FILE_DIR;
		File params_dir = new File(final_dir_path);
		System.out.println("CommonUtils::getParamsDir::path= "
				+ ((null == params_dir) ? "ERROR PATH" : params_dir.getAbsolutePath()));
		return params_dir;
	}

	public static final boolean isSameDay(long source_date, long target_date) {

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(new Date(source_date));
		c2.setTime(new Date(target_date));
		return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
	}

	public static long datatimeStrToLong(String input_datetime_str) {
		long time_stamp = -1;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			time_stamp = format.parse(input_datetime_str).getTime();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return time_stamp;
	}

	public static String getDatetimeString(long input_dt) {
		String output_dt = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			output_dt = sdf.format(new Date(input_dt));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return output_dt;
	}

	public static final double formatDecimal(double input_value) {
		double result = input_value;
		try {
			BigDecimal bd = new BigDecimal(input_value);
			result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public static final ArrayList<BarModel> getBarFromJSON(@Nonnull String input_bar_json, String input_partform,
			String input_cycle) {
		try {
			JSONObject j_object = JSONObject.parseObject(input_bar_json);
			JSONArray k_data_object = j_object.getJSONArray("data");
			if (null != k_data_object && k_data_object.size() > 0) {
				ArrayList<BarModel> bar_list = new ArrayList<BarModel>();

				for (int data_index = 0; data_index < k_data_object.size(); data_index++) {
					JSONObject one_bar_json = k_data_object.getJSONObject(data_index);
					BarModel current_bar = new BarModel();
					current_bar.m_high = formatDecimal(one_bar_json.getDoubleValue("high"));
					current_bar.m_open = formatDecimal(one_bar_json.getDoubleValue("open"));
					current_bar.m_low = formatDecimal(one_bar_json.getDoubleValue("low"));
					current_bar.m_close = formatDecimal(one_bar_json.getDoubleValue("close"));
					current_bar.m_dt = System.currentTimeMillis();
					current_bar.m_platform = input_partform;
					current_bar.m_cycle = input_cycle;
					bar_list.add(current_bar);
					System.out.println("CommonUtils::getBarFromeJSON::bar= " + current_bar);
				}
				return bar_list;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static final TickerModel getTickInfoFromJSON(@Nonnull String input_tick_json) {
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
			t_model.m_ask = formatDecimal(Double.valueOf(ask_str));
			t_model.m_bid = formatDecimal(Double.valueOf(bid_str));
			t_model.m_high = formatDecimal(ticker_obj.getDoubleValue("high"));
			t_model.m_open = formatDecimal(ticker_obj.getDoubleValue("open"));
			t_model.m_low = formatDecimal(ticker_obj.getDoubleValue("low"));
			t_model.m_close = formatDecimal(ticker_obj.getDoubleValue("close"));
			t_model.m_id = ticker_obj.getIntValue("id");
			System.out.println("CommonUtils::getTickInfoFromJSON::ticker= " + t_model);
			return t_model;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

}
