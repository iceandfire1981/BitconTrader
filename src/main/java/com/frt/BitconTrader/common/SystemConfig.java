package com.frt.BitconTrader.common;

public final class SystemConfig {
	public static final String A_KEY = "b8a7f69c-gr4edfki8l-67a067de-58dad";
	public static final String S_KEY = "4799bbc8-c57f2427-d1cf5508-294ee";
	public static final String BASE_URL = "https://api.huobi.de.com";
	public static final String WS_BASE_URL = "https://api.btcgateway.pro";//"https://api.hbdm.com";//
	
	public static final String SYMBOL = "BTC-USD";//"btcusdt";
	public static final String PLATFORM = "HUOBI";
	
	public static final String PERIOD_MIN = "1min";//1min, 5min, 15min, 30min, 60min,4hour,1day, 1mon
	public static final String PERIOD_5MIN = "5min";
	public static final String PERIOD_15MIN = "15min";
	public static final String PERIOD_30MIN = "30min";
	public static final String PERIOD_60MIN = "60min";
	public static final String PERIOD_4H = "4hour";
	public static final String PERIOD_1D = "1day";
	public static final String PERIOD_1MON = "1mon";
	
	public static final String ORDER_OP_BUY = "buy";
	public static final String ORDER_OP_SELL = "sell";
	
	public static final int INPUT_ORDER_OP_BUY 	= 0;
	public static final int INPUT_ORDER_OP_SELL = INPUT_ORDER_OP_BUY + 1;
	
	public static final String mysql_driver = "com.mysql.jdbc.Driver";
	public static final String mysql_url = "jdbc:mysql://127.0.0.1:3306/market_db?serverTimezone=UTC&characterEncoding=utf-8";//"jdbc:mysql://192.168.0.101:3306/market_db?serverTimezone=UTC&characterEncoding=utf-8";
	public static final String user_name = "root";
	public static final String password = "123456";
	
	public static final String BASE_DIR = "D:\\trade_dir\\";
	public static final String PARAM_CONFIG_FILE_DIR = "params";
	public static final String RECORD_SPLIT_CHAR = "\\|";
	
	
	public static final String CREATE_PARAMS = "INSERT INTO param_info (policy_name, entry_diff, stop_loss, take_profit) VALUES (?, ?, ?, ?)";
	public static final String QUERY_PARAMS_BY_POLICY = "SELECT * FROM param_info WHERE policy_name=?";
	public static final String QUERY_PARAMS_BY_ID = "SELECT * FROM param_info WHERE _id=?";
	public static final String UPDATE_PARAMS_TICK = "UPDATE param_info SET order_tick = ?, order_price=?, order_type=?, action_dt= ?  WHERE _id=?";
}
