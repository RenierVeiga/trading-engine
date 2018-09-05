package observers;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;
import com.binance.api.examples.CandleSticksStream;

import factory.StrategyFactory;
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

public class AssetObserver extends CandleSticksStream {

    private String assetA;
    private String quantity;

    private StrategyFactory strategies;

    public AssetObserver(String assetA, String assetB, String quantity) {
	super(String.format("%s%s", assetA, assetB).toUpperCase(), CandlestickInterval.HOURLY);
	this.assetA = assetA;
	this.quantity = quantity;
	strategies = new StrategyFactory(this.getSymbol(), CandlestickInterval.HOURLY);
	Report.createReport("Started Asset Observer for pair: " + this.getSymbol());
    }

    public void update(String quantity) {
	this.quantity = quantity;
	print("Update Balance Event");
	checkPlaceSell();
    }

    private void placeMarketSell() {
	print("Attempt Market Sell");
	AccountInfo.getRestAsyncClient().newOrder(NewOrder.marketSell(this.getSymbol(), quantity), response -> {
	    print("Sell Order Success");
	    Report.createReport("Sell Order Success: \n" + this.toString());
	    Report.createReport(strategies.toString());
	});
    }

    private void checkPlaceSell() {
	print("Check Place Sell ");
	if (strategies.getSellSignal()) {
	    placeMarketSell();
	}
    }

    public String toString() {
	return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
		.append("\nSymbol", this.getSymbol()).append("Date", new Date().toString())
		.append("Quantity", this.quantity).toString();
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
	checkPlaceSell();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.binance.api.examples.CandleSticksCache#closeClient()
     * 
     * Closes this connection as well as connections instantiated by strategies.
     * 
     */
    @Override
    public void closeClient() {
	try {
	    this.clientCloseable.close();
	    strategies.closeClient();
	} catch (IOException e) {
	    e.printStackTrace();
	    Report.createReport("Failed to close socket for: " + this.getSymbol());
	    Report.createReport("Error: " + e.getMessage());
	}

    }
}
