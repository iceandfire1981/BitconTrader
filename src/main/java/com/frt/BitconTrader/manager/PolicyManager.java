package com.frt.BitconTrader.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.frt.BitconTrader.model.BitconParamModel;

public class PolicyManager {
	private HashMap<String, String> m_policy_map;
	private ParamManager m_p_manager;
	
	public PolicyManager(ParamManager p_manager) {
		m_p_manager = p_manager;
		m_policy_map = new HashMap<String, String>();
		m_policy_map.put("bitcon_diff_policy", "com.frt.BitconTrader.policy.BitconPolicy");
	}
	
	public void startManager() {
		for(Map.Entry<String, String> entry : m_policy_map.entrySet()){
		    String p_name = entry.getKey();
		    String p_cls = entry.getValue();
		    ArrayList<BitconParamModel> my_params_list = m_p_manager.getPolicyParams(p_name);
		    if(null != my_params_list && my_params_list.size() > 0) {
		    	for (int param_index = 0; param_index < my_params_list.size(); param_index++) {
		    		BitconParamModel bt_param = my_params_list.get(param_index);
					startOnePolicy(p_cls, bt_param.m_id);
				}
		    }
		}
	}
	
	private void startOnePolicy(String class_name, int param_id) {
		try {
			Class<?> p_cls = Class.forName(class_name);
			Object p_obj = p_cls.newInstance();
			Method[] all_mothod = p_cls.getDeclaredMethods();
			Method set_id_method = p_cls.getMethod("initialPolicy", int.class, ParamManager.class);
			set_id_method.invoke(p_obj, param_id, m_p_manager);
			
			Method start_method = p_cls.getMethod("startPolicy");
			start_method.invoke(p_obj);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
