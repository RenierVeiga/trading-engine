package factory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.market.CandlestickInterval;

import Entities.CandleSticksCache;
import Utils.Enums.StrategyName;
import strategies.BreakoutStrategy;
import strategies.LowerRedCandleStrategy;
import strategies.Strategy;

public class StrategyFactory extends CandleSticksCache {

    private Map<StrategyName, Strategy> strategiesMap = new HashMap<StrategyName, Strategy>();

    public StrategyFactory(String symbol, CandlestickInterval interval) {
	super(symbol, interval);
	strategiesMap.put(StrategyName.BREAKOUT, new BreakoutStrategy(this.getCandlesticksCache()));
	strategiesMap.put(StrategyName.LOWERREDCANDLE, new LowerRedCandleStrategy(this.getCandlesticksCache()));
    }

    public boolean getSellSignal(StrategyName name) {
	return strategiesMap.get(name).getSellSignal();
    }

    public Strategy getStrategy(StrategyName name) {
	return strategiesMap.get(name);
    }

    public boolean getSellSignal() {
	boolean ret = false;
	for (Strategy item : strategiesMap.values()) {
	    ret = ret || item.getSellSignal();
	}
	return ret;
    }

    @Override
    protected void onCandleStickEvent() {
	for (Strategy item : strategiesMap.values()) {
	    item.updateCache(this.getCandlesticksCache());
	}
    }

    public String toString() {
	String str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
		.append("\nSymbol", this.getSymbol()).append("Date", new Date().toString()).toString();

	for (StrategyName item : strategiesMap.keySet()) {
	    str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append(str)
		    .append(item.name(), strategiesMap.get(item).toString()).toString();
	}

	return str;
    }
}
