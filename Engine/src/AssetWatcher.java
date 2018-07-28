import java.util.Set;
import java.util.TreeSet;

import exchangeInfo.BinanceInfo;

/**
 * @author Renier Veiga
 * 
 *         Date: Jul 28, 2018
 * 
 *         First we will determine the trend for BTC. If BTC is trending up we
 *         will liquidate to BTC. If the asset is trending down and BTC is also
 *         trending down we will attempt to liquidate directly to USDT;
 *         otherwise we will liquidate to BTC and then to USDT.
 */

public class AssetWatcher {
	private String asset;
	private Set<String> symbols = BinanceInfo.getSymbols();
	private boolean hasBtcPair = false;
	private boolean hasUsdtPair = false;
	String btcPair = String.format("%sBTC", asset);
	String usdtPair = String.format("%sUSDT", asset);

	public AssetWatcher(String asset) {
		this.asset = asset;
		if (symbols.contains(btcPair)) {
			hasBtcPair = true;
		}
		if (symbols.contains(usdtPair)) {
			hasUsdtPair = true;
		}
	}

	private Set<String> getTradingPairs() {
		Set<String> tradingPairs = new TreeSet<String>();

		return tradingPairs;
	}

	private void startWatch() throws Exception {
		if (hasBtcPair && (BtcUsdSignals.isLiquidateToBtc() || !hasUsdtPair)) {
			// Start watch for BTC pair
		} else if (hasUsdtPair) {
			// Start watch for USDT pair
		} else {
			throw new Exception("Invalid watch state.");
		}
		// TASignals signals = new TASignals(symbol, interval);
	}

}
