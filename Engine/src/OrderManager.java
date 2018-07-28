
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.CandlestickInterval;

import Enums.State;
import accountInfo.AccountInfo;
import reports.Report;

/**
 * @author Renier Veiga
 * 
 *         Date: Mar 15, 2018
 * 
 *         Description: This is intended to handle logic for entering and
 *         exiting a trade. Sells (Exits) are simple and are based on trailing
 *         stops. When a price falls below a certain percentage we exit the
 *         trade. More logic might be added in the future for the exit points of
 *         a sell order. Buys (Entries) are more complex. Since the intent of
 *         this class is to protect the gains, in order to enter a trade some
 *         criteria needs to be met to qualify for a good entry point. At this
 *         time we will enter a trade on a higher low or on the breaking of the
 *         last high price.
 * 
 */

public class OrderManager {
	private String symbol, assetA, assetB;
	private TASignals signals;
	private final CandlestickInterval interval = CandlestickInterval.HOURLY;
	private State state = State.WATCHING;
	// private double quantityP = 1;
	private final double tPercent = 1.05; // Trailing Percentage
	private double curPrice;
	private double sellTrailPrice = 0;
	private boolean useMaxTrailPrice = false;

	public OrderManager(String assetA, String assetB, double quantityP) {
		this.assetA = assetA.toUpperCase();
		this.assetB = assetB.toUpperCase();
		symbol = String.format("%s%s", assetA, assetB).toLowerCase();
		// Init symbol and interval
		signals = new TASignals(symbol, interval);

		// Opens a socket connection and constantly updates the current price while
		// kicking off a price fetch event.
//		AccountManager.getSocketClient().onSingleMarketTickerEvent(symbol, response -> {
//			curPrice = response.getBestBidPrice();
//			if (useMaxTrailPrice) { 
//				sellTrailPrice = Math.max(curPrice, sellTrailPrice);
//			}
//			onPriceFetchEvent();
//		});
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
		});

	}

	public void placeMarketBuy() {
		useMaxTrailPrice = true;
		AccountInfo.getRestAsyncClient().getAccount((Account response) -> {
			String quantity = String.valueOf(Double.parseDouble(response.getAssetBalance(assetB).getFree()) / curPrice);
			NewOrder order = new NewOrder(symbol, OrderSide.BUY, OrderType.MARKET, TimeInForce.GTC, quantity);
			// client.newOrder(order, response -> System.out.println(response));
			Report.createReport(order.toString());
			Report.createReport(this.toString());
		});

	}

	public void checkTrailingStopSell() {
		// Market sell when the market price goes below the last support point or below
		// the trailing price (5% below the max reached value).
		if (curPrice < signals.getLastSupportPoint() || (useMaxTrailPrice && curPrice < sellTrailPrice / tPercent)) {
			placeMarketSell();
			state = State.EXITED;
		}

	}

	public void checkTrailingStopBuy() {
		// Market buy when trend is moving up and the price is above the last high
		if (signals.getBuySignal(curPrice)) {
			placeMarketBuy();
			state = State.ENTERED;
		}

	}

	public void onPriceFetchEvent() {
		if (!state.equals(State.ENTERED)) {
			checkTrailingStopBuy();
		}
		
		if (!state.equals(State.EXITED)) {
			checkTrailingStopSell();
		}
		
		System.out.println(this.toString());
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\nSymbol", symbol.toUpperCase()).append("Date", new Date().toString())
				.append("\nCurrentPrice", curPrice).append("TMaxPrice", sellTrailPrice / tPercent)
				.append("State", state).append("TrendDirection", signals.getTrendDiretion(curPrice))
				.append("\nSignals\n", signals.toString()).toString();
	}

}
