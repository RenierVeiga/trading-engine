
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;

import AccountInfo.AccountInfo;
import reports.Report;

/**
 * @author Renier Veiga
 * 
 *         Date: Mar 15, 2018
 * 
 *         Description: This is intended to handle logic for exiting a trade.
 *         Sells (Exits) are simple and are based on trailing stops and trend
 *         direction. When a price falls below a certain percentage or when the
 *         price goes below a support point, we exit the trade. The intent of
 *         this class is to protect the gains.
 * 
 */

public class AssetManager {
	private String symbol, assetA;
	private TASignals signals;
	private final double tPercent = 1.05; // Trailing Percentage
	private double curPrice;
	private double sellTrailPrice = 0;
	private boolean useMaxTrailPrice = false;
	BinanceApiWebSocketClient bsc = AccountInfo.getSocketClient();

	public AssetManager(String assetA, String assetB) {
		this.assetA = assetA.toUpperCase();
		symbol = String.format("%s%s", assetA, assetB).toLowerCase();
		// Init symbol
		signals = new TASignals(symbol);

		// Opens a socket connection and constantly updates the current price while
		// kicking off a price fetch event.
		bsc.onSingleMarketTickerEvent(symbol, response -> {
			curPrice = response.getBestBidPrice();
			if (useMaxTrailPrice) {
				sellTrailPrice = Math.max(curPrice, sellTrailPrice);
			}
			onPriceFetchEvent();
		});
	}

	public void placeMarketSell() {
		useMaxTrailPrice = false;
		sellTrailPrice = 0;

		AccountInfo.getRestAsyncClient().getAccount((Account response) -> {
			String quantity = String.valueOf(response.getAssetBalance(assetA).getFree());
			NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, quantity);
			// client.newOrder(order, response -> System.out.println(response));
			Report.createReport(order.toString());
			Report.createReport(this.toString());
			disconnect();
		});

	}

	public void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		if (signals.getSellSignal(curPrice) || (useMaxTrailPrice && curPrice < sellTrailPrice / tPercent)) {
			placeMarketSell();
		}

	}

	public void onPriceFetchEvent() {
		checkTrailingStopSell();
	}

	private void disconnect() {
		signals.disconnect();
		bsc.close();
		try {
			// Waits for this thread to end.
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\nSymbol", symbol.toUpperCase()).append("Date", new Date().toString())
				.append("\nCurrentPrice", curPrice).append("TMaxPrice", sellTrailPrice / tPercent)
				.append("TrendDirection", signals.getTrendDiretion(curPrice)).append("\nSignals\n", signals.toString())
				.toString();
	}

}
