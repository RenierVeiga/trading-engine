package observers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.binance.api.connect.BinanceInfo;

import subjects.CandleStickStreamSubject;

public class AccountBalanceObserver {

	/*
	 * Register this class to the balance stream.
	 * 
	 * For each asset determine if exists btc and usd pairs. If asset does not have
	 * a balance unregister both the btc and the usdt pairs from the price stream.
	 * If asset has a balance register the existing pairs (xxxbtc or xxxusdt) to
	 * price stream.
	 */

	private Set<String> symbols = BinanceInfo.getSymbols();

	private Map<String, CandleStickStreamSubject> candleStreamMap = new HashMap<String, CandleStickStreamSubject>();
	private static AccountBalanceObserver instance = new AccountBalanceObserver();

	private AccountBalanceObserver() {
	}

	public void update(Set<String> assets) {
		String btcPair, usdtPair;

		for (String asset : assets) {
			asset = asset.toUpperCase();
			btcPair = String.format("%sBTC", asset);
			usdtPair = String.format("%sUSDT", asset);
			if (symbols.contains(btcPair)) {
				// Register xxxbtc pair.
				candleStreamMap.put(btcPair, new CandleStickStreamSubject(asset, "BTC"));
			}
			if (symbols.contains(usdtPair)) {
				// Register xxxusdt pair.
				candleStreamMap.put(usdtPair, new CandleStickStreamSubject(asset, "USDT"));
			}
		}
		// Unregister assets not in the list.
		for (CandleStickStreamSubject item : candleStreamMap.values()) {
			if (!assets.contains(item.getAssetA())) {
				// Unregister for updates.
				System.out.println("Unregistered: " + item.getSymbol());
				item.unregister();
				candleStreamMap.remove(item.getSymbol());
			}
		}
	}

	public static AccountBalanceObserver getInstance() {
		return instance;
	}

}
