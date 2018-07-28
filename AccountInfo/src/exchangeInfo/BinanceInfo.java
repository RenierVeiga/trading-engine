package exchangeInfo;

import java.util.List;
import java.util.TreeSet;

import com.binance.api.client.domain.general.SymbolInfo;

import AccountInfo.AccountInfo;

public class BinanceInfo {

	private static List<SymbolInfo> symbolInfoList;

	public BinanceInfo() {
		symbolInfoList = AccountInfo.getRestClient().getExchangeInfo().getSymbols();
	}

	public static List<SymbolInfo> getSymbolInfoList() {
		return symbolInfoList;
	}
	
	public static TreeSet<String> getSymbols(){
		// TreeSet ensures that the list is unique. Probably redundant.
		TreeSet<String> symbolList = new TreeSet<String>();
		for(SymbolInfo symbol : symbolInfoList) {
			symbolList.add(symbol.getSymbol());
		}
		return symbolList;
	}

	public static void setSymbolInfoList(List<SymbolInfo> symbols) {
		BinanceInfo.symbolInfoList = symbols;
	}
}
