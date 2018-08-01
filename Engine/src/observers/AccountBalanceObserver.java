package observers;

import java.util.Set;

import com.binance.api.connect.BinanceInfo;

import subjects.PriceStreamSubject;

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

	private PriceStreamSubject psj = PriceStreamSubject.getInstance();
	private static AccountBalanceObserver instance = new AccountBalanceObserver();

	private AccountBalanceObserver() {
	}

	public void update(Set<String> assets) {
		String btcPair, usdtPair;

		for (String asset : assets) {
			btcPair = String.format("%sBTC", asset);
			usdtPair = String.format("%sUSDT", asset);
			if (symbols.contains(btcPair)) {
				// Register xxxbtc pair.
				psj.register(btcPair, new AssetObserver(asset, "BTC"));
			}
			if (symbols.contains(usdtPair)) {
				// Register xxxusdt pair.
				psj.register(usdtPair, new AssetObserver(asset, "USDT"));
			}
		}
		// Unregister assets not in the list.
		for (AssetObserver assetObserver : psj.getObserverMap().values()) {
			if (!assets.contains(assetObserver.getAssetA())) {
				// Close socket connection and unregister for updates.
				assetObserver.disconnect();
				psj.unregister(assetObserver.getSymbol());
			}
		}
	}

	public static AccountBalanceObserver getInstance() {
		return instance;
	}

}
