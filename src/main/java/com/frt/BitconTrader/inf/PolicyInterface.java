package com.frt.BitconTrader.inf;

import com.frt.BitconTrader.manager.ParamManager;

public interface PolicyInterface {

	public String getPolicyName();
	public void   initialPolicy(int param_id, ParamManager param_manager);
	public void   startPolicy();
}
