package observers;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;

import Entities.TASignals;
import properties.Properties;
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

	private final double tPercent = Properties.getInstance().getTrailingPercent();
	private String assetA;
	private String quantity;
	private boolean ignoreTrend = true;

	public AssetObserver(String assetA, String assetB, String quantity) {
		super(String.format("%s%s", assetA, assetB).toUpperCase(), CandlestickInterval.HOURLY);
		this.assetA = assetA;
		this.quantity = quantity;
		Report.createReport("Started Asset Observer for pair: " + this.getSymbol());
	}

	public void update(String quantity) {
		this.quantity = quantity;
		print("Update Balance Event");
		checkTrailingStopSell();
	}

	private void placeMarketSell() {
		print("Attempt Market Sell");
		AccountInfo.getRestAsyncClient().newOrder(NewOrder.marketSell(this.getSymbol(), quantity), response -> {
			Report.createReport("Sell Order Success: \n" + this.toString());
		});
	}

	private void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		if ((!ignoreTrend && this.getSellSignal()) || (this.getClosePrice() < (this.getSellTrailPrice() / tPercent))) {
			placeMarketSell();
		}
		print("Check Trailing stop ");
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\nSymbol", this.getSymbol()).append("Date", new Date().toString())
				.append("\nCurrentPrice", this.getClosePrice())
				.append("TMaxPrice", (this.getSellTrailPrice() / tPercent))
				.append("TrendDirection", this.getTrendDiretion()).append("Last Support", this.getLastSupportPoint())
				.append("Before Last Support", this.getBeforeLastSupportPoint())
				.append("Resitance", this.getLastResistancePoint()).append("Quantity", this.quantity).toString();
	}

	private void print(String message) {
		System.out.println("\n\n ********* Message for: " + this.getSymbol() + " " + message + " *********");
		System.out.println(this.toString());
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
