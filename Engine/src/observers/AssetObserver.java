package observers;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.connect.AccountInfo;

import Entities.TASignals;
import reports.Report;
import subjects.CandleStickStreamSubject.CandleSticksCacheImpl;

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
	private String symbol, assetA;

	public AssetObserver(String assetA, String assetB, CandleSticksCacheImpl candleStickMap) {
		this.assetA = assetA;
		symbol = String.format("%s%s", assetA, assetB).toUpperCase();
		System.out.println("Started Asset Observer for pair: " + symbol);
		signals = new TASignals(candleStickMap.getCandlesticksCache());
		curPrice = candleStickMap.getClosePrice();
	}

	public void update(CandleSticksCacheImpl candleStickMap) {

		signals.updateCandles(candleStickMap.getCandlesticksCache());
		curPrice = candleStickMap.getClosePrice();
		System.out.println("Update Candle event for: " + this.symbol);
		System.out.println("Trend: " + signals.getTrendDiretion(curPrice) + " Support: " + signals.toString());
		if (curPrice > sellTrailPrice) {
			sellTrailPrice = curPrice;
		}
		if (curPrice != null) {
			checkTrailingStopSell();
		}
	}

	private void placeMarketSell() {
		System.out.println("Place Market sell :" + this.toString());
		sellTrailPrice = 0;

		AccountInfo.getRestAsyncClient().getAccount((Account response) -> {
			String quantity = String.valueOf(response.getAssetBalance(assetA).getFree());
			NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, quantity);
			AccountInfo.getRestAsyncClient().newOrder(order,
					orderResponse -> System.out.println(orderResponse.toString()));
			Report.createReport(order.toString());
			Report.createReport(this.toString());
			signals = null;
		});

	}

	private void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		if (signals.getSellSignal(curPrice) || (curPrice < (Double) sellTrailPrice / tPercent)) {
			placeMarketSell();
		}
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\nSymbol", symbol.toUpperCase()).append("Date", new Date().toString())
				.append("\nCurrentPrice", curPrice).append("TMaxPrice", (Double) sellTrailPrice / tPercent)
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
