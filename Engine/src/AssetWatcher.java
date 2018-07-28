import java.util.Set;

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

public class AssetWatcher implements Runnable {
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

	@Override
	public void run() {

		if (hasBtcPair && (BtcUsdSignals.isLiquidateToBtc() || !hasUsdtPair)) {
			// Start watch for BTC pair
			new AssetManager(asset, "BTC");
		} else if (hasUsdtPair) {
			// Start watch for USDT pair
			new AssetManager(asset, "USDT");
		} else {
			// throw new Exception("Invalid watch state.");
		}
	}

}
