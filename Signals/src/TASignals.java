import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.examples.CandlesticksCache;

import Entities.ShortCandle;
import Utils.Enums.TrendDirection;

/**
 * @author Renier Veiga
 * 
 *         Date: Mar 18, 2018
 */
public class TASignals {
	String symbol;
	final CandlestickInterval INTERVAL = CandlestickInterval.HOURLY;
	CandleSticksCacheImpl candleSticksCache;
	Map<Long, Candlestick> candleStickMap;
	ArrayList<Candlestick> candleStickList = new ArrayList<Candlestick>();
	ArrayList<ShortCandle> candleStickMaxList = new ArrayList<ShortCandle>();
	ArrayList<ShortCandle> candleStickMinList = new ArrayList<ShortCandle>();
	int nCandlesToCompare = 20;

	public TASignals(String symbol) {
		this.symbol = symbol;
		candleSticksCache = new CandleSticksCacheImpl(this.symbol, INTERVAL);
		candleStickMap = candleSticksCache.getCandlesticksCache();
		candleStickList = new ArrayList<Candlestick>(candleStickMap.values());
		loadSupResPoints();
	}

	public class CandleSticksCacheImpl extends CandlesticksCache {

		public CandleSticksCacheImpl(String symbol, CandlestickInterval interval) {
			super(symbol, interval);
		}

		public void onCandleStickInitialize() {
			init();
		}

		public void onCandleStickUpdate() {
			update();
		}

	}

	private void init() {

	}

	private void updateLists() {
		loadSupResPoints();
	}

	// This method loads the support and resistance points for a given pair
	private void loadSupResPoints() {
		// Reset list to free memory
		candleStickMaxList = new ArrayList<ShortCandle>();
		candleStickMinList = new ArrayList<ShortCandle>();
		// Iterates over all candles
		int indexOfLastItem = candleStickList.size() - 2;
		for (int i = indexOfLastItem; i > (indexOfLastItem - 4 * nCandlesToCompare);) {
			ShortCandle pMin = null;
			ShortCandle pMax = null;
			// Iterates over the x candles at the time

			for (int c = i; c > i - nCandlesToCompare; c--) {
				ShortCandle previous = new ShortCandle(candleStickList.get(c - 1));
				ShortCandle current = new ShortCandle(candleStickList.get(c));
				ShortCandle next = new ShortCandle(candleStickList.get(c + 1));
				// If (previous < current > next && current > previousMax){maximum found}
				if (current.getHigh() > next.getHigh() && current.getHigh() > previous.getHigh()
						&& (pMax == null || current.getHigh() > pMax.getHigh())) {
					pMax = current;
				}
				// If (previous > current < next && current < previousMin){minimum found}
				if (current.getLow() < next.getLow() && current.getLow() < previous.getLow()
						&& (pMin == null || current.getLow() < pMin.getLow())) {
					pMin = current;
				}
			}
			i = i - (nCandlesToCompare + 1);
			if (pMax != null) {
				if (candleStickMaxList.size() > 0) {
					ShortCandle last = candleStickMaxList.get(candleStickMaxList.size() - 1);
					if (!last.getKey().equals(pMax.getKey())) {
						candleStickMaxList.add(pMax);
					}
				} else {
					candleStickMaxList.add(pMax);
				}
			}
			if (pMin != null) {
				if (candleStickMinList.size() > 0) {
					ShortCandle last = candleStickMinList.get(candleStickMinList.size() - 1);
					if (!last.getKey().equals(pMin.getKey())) {
						candleStickMinList.add(pMin);
					}
				} else {
					candleStickMinList.add(pMin);
				}
			}
		}
	}

	// Update min and max Lists with the last candlestick info.
	private void update() {
		candleStickMap = candleSticksCache.getCandlesticksCache();
		candleStickList = new ArrayList<Candlestick>(candleStickMap.values());
		updateLists();
	}

	public TrendDirection getTrendDiretion(double curPrice) {
		// Using the last few support points we can determine the direction that the
		// trend is going (up or Down).
		// I chose to use the last 2 support points, the current price and the
		// resistance to determine
		// the direction.
		double fSPoint, sSPoint, r1;
		TrendDirection diretion = TrendDirection.UNDETERMINED;
		if (candleStickMinList.size() > 0) {
			fSPoint = candleStickMinList.get(0).getLow();
			sSPoint = candleStickMinList.get(1).getLow();
			r1 = getLastResistancePoint();
			// If (curPrice > resistance > (fSPoint and sSPoint(){then we have a break-up}
			if (curPrice > r1 && r1 > fSPoint && r1 > sSPoint) {
				diretion = TrendDirection.BREAKUP;
			}
			// If (r1 < curPrice > fSPoint > sSPoint){then we have a higher min but no
			// break-up}
			else if (curPrice > fSPoint && fSPoint > sSPoint) {
				diretion = TrendDirection.HIGHERMIN;
			}
			// If (curPrice < fSPoint || sSPoint){then we have a break down}
			else if (curPrice < fSPoint || curPrice < sSPoint) {
				diretion = TrendDirection.BREAKDOWN;
			}
			// Else trend is undetermined.
		}
		return diretion;
	}

	public boolean getBuySignal(double curPrice) {
		// Signal to buy when trend is moving up and the price is above the last high
		if (getTrendDiretion(curPrice) == TrendDirection.BREAKUP) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getSellSignal(double curPrice) {
		// Signal to buy when trend is moving up and the price is above the last high
		if (getTrendDiretion(curPrice) != TrendDirection.BREAKUP
				|| getTrendDiretion(curPrice) != TrendDirection.HIGHERMIN) {
			return true;
		} else {
			return false;
		}
	}

	public double getLastSupportPoint() {
		if (candleStickMinList.size() > 0) {
			return candleStickMinList.get(0).getLow();
		}
		return 0;
	}

	public double getLastResistancePoint() {
		if (candleStickMaxList.size() > 0) {
			return candleStickMaxList.get(0).getHigh();
		}
		return Double.MAX_VALUE;
	}

	public String getLastSupportPoints() {
		ToStringBuilder str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE);
		for (int i = 0; i < candleStickMinList.size(); i++) {
			str.append("Val" + i, candleStickMinList.get(i).getLow());
		}
		return str.toString();
	}

	public String getLastResistancePoints() {
		ToStringBuilder str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE);
		for (int i = 0; i < candleStickMaxList.size(); i++) {
			str.append("Val" + i, candleStickMaxList.get(i).getHigh());
		}
		return str.toString();
	}

	public void disconnect() {
		candleSticksCache.disconnect();
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("\n\tLastSupportPoint", getLastSupportPoint())
				.append("\n\tLastResitancePoint", getLastResistancePoint()).toString();
	}
}
