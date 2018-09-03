package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.binance.api.client.domain.market.Candlestick;

public abstract class Strategy {

    public Strategy(SortedMap<Long, Candlestick> candlesticksCache) {
	this.candlesticksCache = candlesticksCache;
	candleStickList = new ArrayList<Candlestick>(this.getCandlesticksCache().values());
    }

    protected SortedMap<Long, Candlestick> candlesticksCache;
    protected List<Candlestick> candleStickList;

    public void update(SortedMap<Long, Candlestick> candlesticksCache) {
	this.candlesticksCache = candlesticksCache;
	candleStickList = new ArrayList<Candlestick>(this.getCandlesticksCache().values());
	update();
    }

    public abstract void update();

    public abstract boolean getSellSignal();

    public abstract boolean getBuySignal();

    public SortedMap<Long, Candlestick> getCandlesticksCache() {
	return candlesticksCache;
    }

    public double getClosePrice() {
	return Double.parseDouble(getLastCandle().getClose());
    }

    public List<Candlestick> getCandleStickList() {
	return candleStickList;
    }

    public Candlestick getLastCandle() {
	if (candleStickList.size() > 0) {
	    return candleStickList.get(candleStickList.size() - 1);
	} else {
	    return null;
	}
    }

    public Candlestick getPriorCandle() {
	if (candleStickList.size() > 1) {
	    return candleStickList.get(candleStickList.size() - 2);
	} else {
	    return null;
	}
    }

    protected void print(String message) {
	System.out.println("\n\n ********* Message " + message + " *********");
	System.out.println(this.toString());
    }
}
