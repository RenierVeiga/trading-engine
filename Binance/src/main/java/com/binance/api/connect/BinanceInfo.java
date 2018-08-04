package com.binance.api.connect;

import java.util.List;
import java.util.TreeSet;

import com.binance.api.client.domain.general.SymbolInfo;

public class BinanceInfo {

	private static List<SymbolInfo> symbolInfoList = AccountInfo.getRestClient().getExchangeInfo().getSymbols();

	private static TreeSet<String> symbolList = new TreeSet<String>();

	static {
		for (SymbolInfo symbol : symbolInfoList) {
			symbolList.add(symbol.getSymbol().toUpperCase());
		}
	}

	public static List<SymbolInfo> getSymbolInfoList() {
		return symbolInfoList;
	}

	public static TreeSet<String> getSymbols() {
		return symbolList;
	}

}
