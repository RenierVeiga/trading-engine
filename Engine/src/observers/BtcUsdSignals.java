package observers;

import java.util.Map;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;
import com.binance.api.examples.CandleSticksCache;

import Entities.TASignals;
import Utils.Enums.TrendDirection;

public class BtcUsdSignals {
	private static final String SYMBOL = "BTCUSDT";
	private static TASignals signals;
	private static CandleSticksCacheImpl candleCache;
	private static Double curPrice;
	private static BtcUsdSignals instance = new BtcUsdSignals();

	private BtcUsdSignals() {
		BtcUsdSignals.candleCache = new CandleSticksCacheImpl(SYMBOL, CandlestickInterval.FOUR_HOURLY);
		signals = new TASignals(candleCache.getCandlesticksCache());
		curPrice = Double.parseDouble(AccountInfo.getRestClient().getPrice(SYMBOL).getPrice());
	}

	private class CandleSticksCacheImpl extends CandleSticksCache {

		public CandleSticksCacheImpl(String symbol, CandlestickInterval interval) {
			super(symbol, interval);
		}

		@Override
		public void onCandleStickEvent() {
			updateCandle(this.getCandlesticksCache());
		}
	}

	public void updateCandle(Map<Long, Candlestick> candleStickMap) {
		System.out.println("Update Candle event for: " + SYMBOL);
		signals.updateCandles(candleStickMap);
	}

	public void updatePrice(Double curPrice) {
		System.out.println("Update Price event for: " + SYMBOL);
		BtcUsdSignals.curPrice = curPrice;
	}

	public static TrendDirection getTrend() {
		return signals.getTrendDiretion(curPrice);
	}

	public static synchronized boolean isLiquidateToBtc() {
		TrendDirection trend = getTrend();
		return (trend.equals(TrendDirection.BREAKUP) || trend.equals(TrendDirection.HIGHERMIN));
	}

	public static BtcUsdSignals getInstance() {
		return instance;
	}
}
