package observers;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;

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

public class AssetObserver extends TASignals {

	private final double tPercent = 1.05; // Trailing Percentage
	private String symbol, assetA;
	private String quantity;

	public AssetObserver(String assetA, String assetB, String quantity) {
		super(String.format("%s%s", assetA, assetB).toUpperCase(), CandlestickInterval.HOURLY);
		this.assetA = assetA;
		this.quantity = quantity;
		symbol = String.format("%s%s", assetA, assetB).toUpperCase();
		Report.createReport("Started Asset Observer for pair: " + symbol);
	}

	public void update(String quantity) {
		this.quantity = quantity;
		print("Update Balance Event");
		checkTrailingStopSell();
	}

	private void placeMarketSell() {
		AccountInfo.getRestAsyncClient().newOrder(NewOrder.marketSell(symbol, quantity), response -> {
			Report.createReport("Sell Order Success: \n" + this.toString());
		});
	}

	private void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		if (this.getSellSignal() || (this.getClosePrice() < (this.getSellTrailPrice() / tPercent))) {
			placeMarketSell();
		}
		print("Check Trailing stop ");
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\nSymbol", symbol.toUpperCase()).append("Date", new Date().toString())
				.append("\nCurrentPrice", this.getClosePrice())
				.append("TMaxPrice", (this.getSellTrailPrice() / tPercent))
				.append("TrendDirection", this.getTrendDiretion()).toString();
	}

	private void print(String message) {
		System.out.println("Message for: " + this.symbol + " " + message);
		System.out.println("Trailling price = " + this.getSellTrailPrice() / tPercent);
		System.out.println("Trend: " + this.getTrendDiretion() + " Support: " + this.toString());
	}

	public String getSymbol() {
		return symbol;
	}

	public String getAssetA() {
		return assetA;
	}

	@Override
	public void onCandleStickEvent() {
		// TODO Auto-generated method stub
		this.loadSupResPoints();
		checkTrailingStopSell();
	}
}
