package com.frt.BitconTrader.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.frt.BitconTrader.common.CommonUtils;
import com.frt.BitconTrader.common.SystemConfig;
import com.frt.BitconTrader.model.BitconParamModel;

public class ParamManager {

	private Connection m_db_connect;

	public ParamManager(Connection db_connect) {
		m_db_connect = db_connect;
		loadParams();
	}

	public ArrayList<BitconParamModel> getPolicyParams(String my_policy_name) {
		ArrayList<BitconParamModel> all_my_params = new ArrayList<BitconParamModel>();
		PreparedStatement query_s = null;
		ResultSet query_records = null;
		int record_count = 0;
		try {
			query_s = m_db_connect.prepareStatement(SystemConfig.QUERY_PARAMS_BY_POLICY);
			query_s.setString(1, my_policy_name);
			query_records = query_s.executeQuery();
			while (query_records.next()) {
				BitconParamModel bp_param = new BitconParamModel();
				bp_param.m_id = query_records.getInt("_id");
				bp_param.m_policy_name = query_records.getString("policy_name");
				bp_param.m_diff = query_records.getDouble("entry_diff");
				bp_param.m_stop_loss = query_records.getDouble("stop_loss");
				bp_param.m_take_profit = query_records.getDouble("take_profit");
				bp_param.m_order_tick = query_records.getString("order_tick");
				bp_param.m_order_price = query_records.getDouble("order_price");
				bp_param.m_order_type = query_records.getInt("order_type");
				bp_param.m_order_action_dt = query_records.getLong("action_dt");

				all_my_params.add(bp_param);
				record_count = record_count + 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (null != query_records)
					query_records.close();
				if (null != query_s)
					query_s.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

		if (record_count > 0) {
			return all_my_params;
		} else {
			return null;
		}

	}
	
	public BitconParamModel getPolicyParams(int param_id) {
		ArrayList<BitconParamModel> all_my_params = new ArrayList<BitconParamModel>();
		PreparedStatement query_s = null;
		ResultSet query_records = null;
		int record_count = 0;
		try {
			query_s = m_db_connect.prepareStatement(SystemConfig.QUERY_PARAMS_BY_ID);
			query_s.setInt(1, param_id);
			query_records = query_s.executeQuery();
			while (query_records.next()) {
				BitconParamModel bp_param = new BitconParamModel();
				bp_param.m_id = query_records.getInt("_id");
				bp_param.m_policy_name = query_records.getString("policy_name");
				bp_param.m_diff = query_records.getDouble("entry_diff");
				bp_param.m_stop_loss = query_records.getDouble("stop_loss");
				bp_param.m_take_profit = query_records.getDouble("take_profit");
				bp_param.m_order_tick = query_records.getString("order_tick");
				bp_param.m_order_price = query_records.getDouble("order_price");
				bp_param.m_order_type = query_records.getInt("order_type");
				bp_param.m_order_action_dt = query_records.getLong("action_dt");

				all_my_params.add(bp_param);
				record_count = record_count + 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (null != query_records)
					query_records.close();
				if (null != query_s)
					query_s.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

		if (record_count > 0) {
			return all_my_params.get(0);
		} else {
			return null;
		}

	}

	public void updateOrderTick(int record_id, String order_tick, double order_price, int order_type, long action_dt) {
		PreparedStatement update_s = null;
		int effect_count = -1;

		try {
			update_s = m_db_connect.prepareStatement(SystemConfig.UPDATE_PARAMS_TICK);
			update_s.setString(1, order_tick);
			update_s.setDouble(2, order_price);
			update_s.setInt(3, order_type);
			update_s.setLong(4, action_dt);
			update_s.setInt(5, record_id);
			effect_count = update_s.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (null != update_s)
					update_s.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		System.out.println("ParamManager::updateOrderTick::id= " + record_id + " order_tick= " + order_tick
				+ " order_price= " + order_price + " order_type= " + order_type + " result= " + effect_count);
	}

	private void loadParams() {
		File param_dir = CommonUtils.getParamsDir();
		File[] all_files = param_dir.listFiles();
		System.out.println("BitconPolicy::initialPolicy::file_total= " + (null == all_files ? -1 : all_files.length));
		if (null != all_files && all_files.length > 0) {
			for (File current_file : all_files) {
				FileReader record_file_reader = null;
				BufferedReader record_buffer_reader = null;
				try {
					record_file_reader = new FileReader(current_file);
					record_buffer_reader = new BufferedReader(record_file_reader);
					String line = null;
					while (((line = record_buffer_reader.readLine()) != null)) {
						System.out.println("BitconPolicy::initialPolicy::line= " + line);
						String[] all_fields = line.split(SystemConfig.RECORD_SPLIT_CHAR);
						createParam(all_fields[0], Double.valueOf(all_fields[1]), Double.valueOf(all_fields[2]),
								Double.valueOf(all_fields[3]));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					try {
						if (null != record_buffer_reader)
							record_buffer_reader.close();
						if (null != record_file_reader)
							record_file_reader.close();
						current_file.delete();
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			}
		}
	}
	
	private void createParam(String current_policy, double entry_diff, double stop_loss, double take_profit) {
		PreparedStatement insert_s = null;
		int effert_count = -1;

		try {
			insert_s = m_db_connect.prepareStatement(SystemConfig.CREATE_PARAMS);
			insert_s.setString(1, current_policy);
			insert_s.setDouble(2, entry_diff);
			insert_s.setDouble(3, stop_loss);
			insert_s.setDouble(4, take_profit);
			effert_count = insert_s.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (null != insert_s)
					insert_s.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

		System.out.println("ParamManager::createParam::diff= " + entry_diff + " sl= " + stop_loss + " tp= "
				+ take_profit + " result= " + effert_count);
	}
}
