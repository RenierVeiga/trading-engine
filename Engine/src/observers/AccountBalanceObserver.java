package observers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.connect.BinanceInfo;

import reports.Report;

public class AccountBalanceObserver {

	/*
	 * Register this class to the balance stream.
	 * 
	 * For each asset determine if exists btc and usd pairs. If asset does not have
	 * a balance unregister both the btc and the usdt pairs from the price stream.
	 * If asset has a balance register the existing pairs (xxxbtc or xxxusdt) to
	 * price stream.
	 */

	private Map<String, Map<FilterType, SymbolFilter>> symbolInfoMap = BinanceInfo.getSymbolFilterMap();

	private Map<String, AssetObserver> assetObserverMap = new HashMap<String, AssetObserver>();
	private static AccountBalanceObserver instance = new AccountBalanceObserver();

	private AccountBalanceObserver() {
	}

	public void update(Map<String, AssetBalance> assetBalanceMap) {
		String btcPair, usdtPair;

		// Iterate over the registration list.
		for (AssetObserver item : new ArrayList<AssetObserver>(assetObserverMap.values())) {
			Double availableBalance = Double.parseDouble(assetBalanceMap.get(item.getAssetA()).getFree());
			String symbol = item.getSymbol();
			if (availableBalance < BinanceInfo.getMinQuantity(symbol)) {
				// Unregister for updates.
				Report.createReport("Unregistered: " + symbol);

				item.closeClient();
				assetObserverMap.remove(symbol);
				item = null;
			}
		}

		// Iterate over balance list.
		for (AssetBalance assetBalance : assetBalanceMap.values()) {
			String asset = assetBalance.getAsset().toUpperCase();
			btcPair = String.format("%sBTC", asset);
			usdtPair = String.format("%sUSDT", asset);

			// Verify that this trading pair exists.
			if (symbolInfoMap.keySet().contains(btcPair)) {
				updateBalanceandRegister(assetBalance, btcPair, asset, "BTC");
			}

			// Verify that this trading pair exists.
			if (symbolInfoMap.keySet().contains(usdtPair)) {
				updateBalanceandRegister(assetBalance, usdtPair, asset, "USDT");
			}
		}
	}

	private void updateBalanceandRegister(AssetBalance assetBalance, String symbol, String assetA, String assetB) {
		// If balance > then minimal allowed to trade then register pair.
		if (Double.parseDouble(assetBalance.getFree()) >= BinanceInfo.getMinQuantity(symbol)) {
			String balance = getBalance(symbol, assetBalance);
			Report.createReport("Balance update for: " + assetA);
			// If we are already watching it then update the balance.
			AssetObserver observer = assetObserverMap.get(symbol);
			if (observer != null) {
				observer.update(balance);
			}
			// Otherwise add to watch list.
			else {
				assetObserverMap.put(symbol, new AssetObserver(assetA, assetB, balance));
			}
		}
	}

	private String getBalance(String symbol, AssetBalance assetBalance) {
		String stepSize = symbolInfoMap.get(symbol).get(FilterType.LOT_SIZE).getStepSize();
		int fIndex = stepSize.indexOf('.');
		int lIndex = stepSize.indexOf('1');
		int nDecimalPlaces = 0;
		if (lIndex > fIndex) {
			nDecimalPlaces = stepSize.substring(fIndex, lIndex).length();
		}
		double balance = roundAvoid(Double.parseDouble(assetBalance.getFree()), nDecimalPlaces);
		return String.valueOf(balance);
	}

	private double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.floor(value * scale) / scale;
	}

	public static AccountBalanceObserver getInstance() {
		return instance;
	}

}
