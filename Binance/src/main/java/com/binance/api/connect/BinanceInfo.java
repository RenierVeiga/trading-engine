package com.binance.api.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;

public class BinanceInfo {

	private static List<SymbolInfo> symbolInfoList = AccountInfo.getRestClient().getExchangeInfo().getSymbols();

	private static Map<String, Map<FilterType, SymbolFilter>> symbolFilterMap = new HashMap<String, Map<FilterType, SymbolFilter>>();

	static {
		for (SymbolInfo symbol : symbolInfoList) {
			Map<FilterType, SymbolFilter> filterMap = new HashMap<FilterType, SymbolFilter>();
			for (SymbolFilter filter : symbol.getFilters()) {
				filterMap.put(filter.getFilterType(), filter);
			}
			symbolFilterMap.put(symbol.getSymbol().toUpperCase(), filterMap);
		}
	}

	public static List<SymbolInfo> getSymbolInfoList() {
		return symbolInfoList;
	}

	public static Set<String> getSymbols() {
		return symbolFilterMap.keySet();
	}

	public static Map<String, Map<FilterType, SymbolFilter>> getSymbolFilterMap() {
		return symbolFilterMap;
	}

	public static Double getMinQuantity(String symbol) {
		Double minQuantity = Double
				.parseDouble(symbolFilterMap.get(symbol).get(FilterType.MIN_NOTIONAL).getMinNotional());
		return minQuantity;
	}

}
