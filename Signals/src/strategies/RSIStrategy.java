package strategies;

import java.util.SortedMap;

import com.binance.api.client.domain.market.Candlestick;

public class RSIStrategy extends Strategy {

    public RSIStrategy(SortedMap<Long, Candlestick> candlesticksCache) {
	super(candlesticksCache);
	// TODO Auto-generated constructor stub
    }

    @Override
    public void update() {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean getSellSignal() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean getBuySignal() {
	// TODO Auto-generated method stub
	return false;
    }

}
