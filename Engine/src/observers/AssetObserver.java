package observers;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;
import com.binance.api.examples.CandleSticksCache;

import Entities.TASignals;
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

public class AssetObserver {

	private TASignals signals;
	private final double tPercent = 1.05; // Trailing Percentage
	private Double curPrice;
	private double sellTrailPrice = 0;
	private boolean useMaxTrailPrice = false;
	private String symbol, assetA;
	private CandleSticksCacheImpl candleCache;

	public AssetObserver(String assetA, String assetB) {
		this.assetA = assetA;
		symbol = String.format("%s%s", assetA, assetB).toLowerCase();
		System.out.println("Started Asset Observer for pair: " + symbol);
		candleCache = new CandleSticksCacheImpl(symbol, CandlestickInterval.FOUR_HOURLY);
		new TASignals(candleCache.getCandlesticksCache());
	}

	public class CandleSticksCacheImpl extends CandleSticksCache {

		public CandleSticksCacheImpl(String symbol, CandlestickInterval interval) {
			super(symbol, interval);
		}

		@Override
		public void onCandleStickEvent() {
			updateCandle(this.getCandlesticksCache());
		}
	}

	public void updateCandle(Map<Long, Candlestick> candleStickMap) {
		signals.updateCandles(candleStickMap);
		if (curPrice != null) {
			checkTrailingStopSell();
		}
		System.out.println("Update Candle event: " + this.toString());
	}

	public void disconnect() {
		candleCache.disconnect();
	}

	private void placeMarketSell() {
		System.out.println("Place Market sell :" + this.toString());
		useMaxTrailPrice = false;
		sellTrailPrice = 0;

		AccountInfo.getRestAsyncClient().getAccount((Account response) -> {
			String quantity = String.valueOf(response.getAssetBalance(assetA).getFree());
			NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, quantity);
			// client.newOrder(order, response -> System.out.println(response));
			Report.createReport(order.toString());
			Report.createReport(this.toString());
		});

	}

	private void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		System.out.println("Check trailling stop sell: " + this.toString());
		if (signals.getSellSignal(curPrice) || (useMaxTrailPrice && curPrice < sellTrailPrice / tPercent)) {
			placeMarketSell();
		}

	}

	public void updatePrice(double price) {
		curPrice = price;
		checkTrailingStopSell();
		System.out.println("Update Price event: " + this.toString());
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\nSymbol", symbol.toUpperCase()).append("Date", new Date().toString())
				.append("\nCurrentPrice", curPrice).append("TMaxPrice", sellTrailPrice / tPercent)
				.append("TrendDirection", signals.getTrendDiretion(curPrice)).append("\nSignals\n", signals.toString())
				.toString();
	}

	public String getSymbol() {
		return symbol;
	}

	public String getAssetA() {
		return assetA;
	}

}
