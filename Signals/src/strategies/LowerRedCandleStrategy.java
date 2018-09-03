package strategies;

import java.util.SortedMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.market.Candlestick;

import Entities.ShortCandle;

/**
 * @author Renier Veiga
 * 
 *         Date: Aug 26, 2018
 * 
 *         The purpose of this is to determine when the price is trading below
 *         the last candle's lowest price. Trading below a previous candle's low
 *         is a bearish sign, and this will serve to signal a potential sell
 *         point.
 */
public class LowerRedCandleStrategy extends Strategy {

    Long initTime = null;

    public LowerRedCandleStrategy(SortedMap<Long, Candlestick> candleStickMap) {
	super(candleStickMap);
	initTime = this.getLastCandle().getOpenTime();
    }

    @Override
    public boolean getSellSignal() {
	boolean ret = false;
	// Check to see if at least one candle has elapsed in order to allow buying at
	// the low. This ensures that we do not sell before at least one candle has
	// elapsed.
	if (initTime != null && initTime < this.getLastCandle().getOpenTime().longValue()
		&& this.getPriorCandle() != null) {
	    // If the previous candle is red and the current price is below the last
	    // candle's low.
	    ShortCandle priorCandle = new ShortCandle(this.getPriorCandle());
	    if (priorCandle.isRed() && this.getClosePrice() < priorCandle.getLow()) {
		ret = true;
	    }
	}
	return ret;
    }

    @Override
    public boolean getBuySignal() {
	// This class does not implement this.
	return false;
    }

    public String toString() {
	return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
		.append("\n\tClose Price", this.getClosePrice()).append("\n\tInitTime", initTime)
		.append("\n\tLastCandle Opentime", getLastCandle().getOpenTime())
		.append("\n\tGetSellSignal", getSellSignal()).append("\n\tGetBuySignal", this.getBuySignal())
		.toString();
    }

    @Override
    public void update() {
    }
}
