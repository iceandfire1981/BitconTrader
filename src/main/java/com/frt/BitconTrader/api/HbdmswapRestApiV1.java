package com.frt.BitconTrader.api;

import com.frt.BitconTrader.api.request.Order;
import com.frt.BitconTrader.api.util.HbdmHttpClient;
import com.frt.BitconTrader.common.SystemConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HbdmswapRestApiV1 implements IHbdmswapRestApi {
	
	public static final String HUOBI_FUTURE_TICKER = "/swap-ex/market/detail/merged";
	public static final String HUOBI_FUTURE_DEPTH = "/swap-ex/market/depth";
	public static final String HUOBI_FUTURE_KLINE = "/swap-ex/market/history/kline";
	public static final String HUOBI_FUTURE_TRADE = "/swap-ex/market/history/trade";

	public static final String HUOBI_FUTURE_CONTRACT_INFO = "/swap-api/v1/swap_contract_info";
	public static final String HUOBI_FUTURE_CONTRACT_INDEX = "/swap-api/v1/swap_index";
	public static final String HUOBI_FUTURE_CONTRACT_PRICE_LIMIT = "/swap-api/v1/swap_price_limit";
	public static final String HUOBI_FUTURE_CONTRACT_OPEN_INTEREST = "/swap-api/v1/swap_open_interest";
	public static final String HUOBI_FUTURE_CONTRACT_ORDER_DETAIL = "/swap-api/v1/swap_order_detail";
	public static final String HUOBI_FUTURE_CONTRACT_HISORDERS = "/swap-api/v1/swap_hisorders";
	public static final String HUOBI_FUTURE_CONTRACT_BATCHORDER = "/swap-api/v1/swap_batchorder";
	public static final String HUOBI_FUTURE_ACCOUNT_INFO = "/swap-api/v1/swap_account_info";
	public static final String HUOBI_FUTURE_POSITION_INFO = "/swap-api/v1/swap_position_info";
	public static final String HUOBI_FUTURE_ORDER = "/swap-api/v1/swap_order";
	public static final String HUOBI_FUTURE_ORDER_CANCEL = "/swap-api/v1/swap_cancel";
	public static final String HUOBI_FUTURE_ORDER_INFO = "/swap-api/v1/swap_order_info";
	public static final String HUOBI_FUTURE_ORDER_CANCEL_ALL = "/swap-api/v1/swap_cancelall";
	public static final String HUOBI_CONTRACE_CODE = "/swap-api/v1/swap_open_interest";
	public static final String HUOBI_CONTRACE_OPENORDERS = "/swap-api/v1/swap_openorders";
	
	public static final String HUOBI_CONTRACE_TRIGGER_ORDER = "/swap-api/v1/swap_trigger_order";
	public static final String HUOBI_CONTRACE_CANCEL_TRIGGER_ORDER = "/swap-api/v1/swap_trigger_cancel";
	public static final String HUOBI_GET_TRIGGER_ORDERS = "/swap-api/v1/swap_trigger_openorders";
	public static final String HUOBI_GET_HISTORY_TRIGGER_ORDERS = "/swap-api/v1/swap_trigger_hisorders";

	private String secret_key;
	private String api_key;
	private String url_prex;
	
	public HbdmswapRestApiV1(String url_prex, String api_key, String secret_key) {
		this.api_key = api_key;
		this.secret_key = secret_key;
		this.url_prex = url_prex;
	}

	public HbdmswapRestApiV1(String url_prex) {
		this.url_prex = url_prex;

	}
	
	@Override
	public String futureContractInfo(String contractCode) {
		Map<String, String> params = new HashMap<>();
		
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		String contractinfoRes = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_CONTRACT_INFO, params);
		return contractinfoRes;
	}

	@Override
	public String futureContractIndex(String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		String contractindexRes = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_CONTRACT_INDEX,
				params);
		return contractindexRes;
	}

	@Override
	public String futurePriceLimit(String contractCode)
			throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		String contractinfoRes = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_CONTRACT_PRICE_LIMIT,
				params);
		return contractinfoRes;
	}

	@Override
	public String futureOpenInterest(String contractCode)
			throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		String contractinfoRes = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_CONTRACT_OPEN_INTEREST,
				params);
		return contractinfoRes;
	}

	public String futureMarketDepth(String contractCode, String type) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(type)) {
			params.put("type", type);
		}
		String contractinfoRes = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_DEPTH, params);
		return contractinfoRes;
	}

	public String futureMarketHistoryKline(String contractCode, String period,String size) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(period)) {
			params.put("period", period);
		}
		if (!StringUtils.isEmpty(size)) {
			params.put("size", size);
		}
		String res = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_KLINE, params);
		return res;
	}

	public String futureMarketDetailMerged(String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}		
		String res = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_TICKER, params);
		return res;
	}

	public String futureMarketDetailTrade(String contractCode, String size) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(size)) {
			params.put("size", size);
		}
		String res = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_TRADE, params);
		return res;
	}

	public String futureMarketHistoryTrade(String contractCode, String size) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(size)) {
			params.put("size", size);
		}
		String res = HbdmHttpClient.getInstance().doGet(url_prex + HUOBI_FUTURE_TRADE, params);
		return res;
	}

	public String futureContractAccountInfo(String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}

		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_ACCOUNT_INFO, params, new HashMap<>());
		return res;
	}

	public String futureContractPositionInfo(String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}

		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_POSITION_INFO, params, new HashMap<>());
		return res;
	}

	public String futureContractOrder(String contractCode, String clientOrderId,
			String price, String volume, String direction, String offset, String leverRate, String orderPriceType)
			throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();		
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(clientOrderId)) {
			params.put("client_order_id", clientOrderId);
		}
		if (!StringUtils.isEmpty(price)) {
			params.put("price", price);
		}
		if (!StringUtils.isEmpty(volume)) {
			params.put("volume", volume);
		}
		if (!StringUtils.isEmpty(direction)) {
			params.put("direction", direction);
		}
		if (!StringUtils.isEmpty(offset)) {
			params.put("offset", offset);
		}
		if (!StringUtils.isEmpty(leverRate)) {
			params.put("lever_rate", leverRate);
		}
		if (!StringUtils.isEmpty(orderPriceType)) {
			params.put("order_price_type", orderPriceType);
		}
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST", url_prex + HUOBI_FUTURE_ORDER,
				params, new HashMap<>());
		return res;
	}

	public String futureContractBatchorder(List<Order> orders) throws HttpException, IOException {
		Map<String, Object> params = new HashMap<>();
		params.put("orders_data", orders);
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST", url_prex + HUOBI_FUTURE_CONTRACT_BATCHORDER,
				params, new HashMap<>());
		return res;
	}

	public String futureContractCancel(String orderId, String clientOrderId,String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(orderId)) {
			params.put("order_id", orderId);
		}
		if (!StringUtils.isEmpty(clientOrderId)) {
			params.put("client_order_id", clientOrderId);
		}
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_ORDER_CANCEL, params, new HashMap<>());
		return res;
	}

	public String futureContractCancelall(String contractCode) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}

		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_ORDER_CANCEL_ALL, params, new HashMap<>());
		return res;
	}

	public String futureContractOrderInfo(String orderId, String clientOrderId,String contractCode,String orderType) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(orderId)) {
			params.put("order_id", orderId);
		}
		if (!StringUtils.isEmpty(clientOrderId)) {
			params.put("client_order_id", clientOrderId);
		}
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(orderType)) {
			params.put("order_type", orderType);
		}	
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_ORDER_INFO, params, new HashMap<>());
		return res;
	}

	public String futureContractOrderDetail(String contractCode, String orderId, String pageIndex, String pageSize,String createdAt,String orderType)
			throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(orderId)) {
			params.put("order_id", orderId);
		}
		if (!StringUtils.isEmpty(pageIndex)) {
			params.put("page_index", pageIndex);
		}
		if (!StringUtils.isEmpty(pageSize)) {
			params.put("page_size", pageSize);
		}
		if (!StringUtils.isEmpty(createdAt)) {
			params.put("created_at", createdAt);
		}
		if (!StringUtils.isEmpty(orderType)) {
			params.put("order_type", orderType);
		}
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_CONTRACT_ORDER_DETAIL, params, new HashMap<>());
		return res;
	}

	public String futureContractOpenorders(String contractCode, String pageIndex, String pageSize)
			throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(pageIndex)) {
			params.put("page_index", pageIndex);
		}
		if (!StringUtils.isEmpty(pageSize)) {
			params.put("page_size", pageSize);
		}
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_CONTRACE_OPENORDERS, params, new HashMap<>());
		return res;
	}

	public String futureContractHisorders(String contractCode, String tradeType, String type, String status,
			String createDate, String pageIndex, String pageSize) throws HttpException, IOException {
		Map<String, String> params = new HashMap<>();
		if (!StringUtils.isEmpty(contractCode)) {
			params.put("contract_code", contractCode);
		}
		if (!StringUtils.isEmpty(tradeType)) {
			params.put("trade_type", tradeType);
		}
		if (!StringUtils.isEmpty(type)) {
			params.put("type", type);
		}
		if (!StringUtils.isEmpty(createDate)) {
			params.put("create_date", createDate);
		}
		if (!StringUtils.isEmpty(status)) {
			params.put("status", status);
		}
		if (!StringUtils.isEmpty(pageIndex)) {
			params.put("page_index", pageIndex);
		}
		if (!StringUtils.isEmpty(pageSize)) {
			params.put("page_size", pageSize);
		}
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_FUTURE_CONTRACT_HISORDERS, params, new HashMap<>());
		return res;
	}

	@Override
	public String futureContractTriggerOrder(String symbol, String trigger_type, String trigger_price,
			String order_price, String volume, String direction, String offset, String lever_rate) throws HttpException, IOException {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<>();
		if(!StringUtils.isEmpty(symbol)) {
			params.put("contract_code", symbol);
		} else {
			params.put("contract_code", SystemConfig.SYMBOL);
		}
		
		if(!StringUtils.isEmpty(trigger_type)) {
			params.put("trigger_type", trigger_type);
		} else {
			params.put("trigger_type", SystemConfig.TRIGGER_TYPE_GE);
		}
		
		if(!StringUtils.isEmpty(trigger_price)) {
			params.put("trigger_price", trigger_price);
		}
		
		if(!StringUtils.isEmpty(order_price)) {
			params.put("order_price", order_price);
		}
		
		if(!StringUtils.isEmpty(volume)) {
			params.put("volume", volume);
		} else {
			params.put("volume", "1");
		}
		
		if(!StringUtils.isEmpty(direction)) {
			params.put("direction", direction);
		} else {
			params.put("direction", SystemConfig.ORDER_OP_BUY);
		}
		
		if(!StringUtils.isEmpty(offset)) {
			params.put("offset", offset);
		} else {
			params.put("offset", SystemConfig.OFFSET_TYPE_CLOSE);
		}
		
		if(!StringUtils.isEmpty(lever_rate)) {
			params.put("lever_rate", lever_rate);
		} else {
			params.put("lever_rate", "1");
		}
		
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_CONTRACE_TRIGGER_ORDER, params, new HashMap<>());
		
		return res;
	}

	@Override
	public String futureCancelTriggerOrder(String symbol, @NonNull String order_id) throws HttpException, IOException {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<>();
		if(!StringUtils.isEmpty(symbol)) {
			params.put("contract_code", symbol);
		} else {
			params.put("contract_code", SystemConfig.SYMBOL);
		}
		
		if(!StringUtils.isEmpty(order_id)) {
			params.put("order_id", order_id);
		} else {
			params.put("order_id", order_id);
		}
		
		
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_CONTRACE_CANCEL_TRIGGER_ORDER, params, new HashMap<>());
		
		return res;
	}

	@Override
	public String futureGetTriggerOrders(String symbol, String page_index, String page_size)
			throws HttpException, IOException {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<>();
		if(!StringUtils.isEmpty(symbol)) {
			params.put("contract_code", symbol);
		} else {
			params.put("contract_code", SystemConfig.SYMBOL);
		}
		
		if(!StringUtils.isEmpty(page_index)) {
			params.put("page_index", page_index);
		} 
		
		if(!StringUtils.isEmpty(page_size)) {
			params.put("page_size", page_size);
		}
		
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_GET_TRIGGER_ORDERS, params, new HashMap<>());
		return res;
	}

	@Override
	public String futureGetHistoryTriggerOrders(String contract_code, String trade_type, String status,
			String create_date) throws HttpException, IOException {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<>();
		if(!StringUtils.isEmpty(contract_code)) {
			params.put("contract_code", contract_code);
		} else {
			params.put("contract_code", SystemConfig.SYMBOL);
		}
		
		if(!StringUtils.isEmpty(trade_type)) {
			params.put("trade_type", trade_type);
		} else {
			params.put("trade_type", "0");
		}
		
		if(!StringUtils.isEmpty(status)) {
			params.put("status", status);
		} else {
			params.put("status", "0");
		}
		
		if(!StringUtils.isEmpty(create_date)) {
			params.put("create_date", create_date);
		} else {
			params.put("create_date", "1");
		}
		
		String res = HbdmHttpClient.getInstance().call(api_key, secret_key, "POST",
				url_prex + HUOBI_GET_HISTORY_TRIGGER_ORDERS, params, new HashMap<>());
		return res;
	}

}
