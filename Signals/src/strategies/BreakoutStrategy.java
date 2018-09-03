package strategies;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.market.Candlestick;

import Entities.ShortCandle;
import Utils.Enums.TrendDirection;

/**
 * @author Renier Veiga
 * 
 *         Date: Mar 18, 2018
 */
public class BreakoutStrategy extends Strategy {

    public BreakoutStrategy(SortedMap<Long, Candlestick> candleStickMap) {
	super(candleStickMap);
	loadSupResPoints();
    }

    private Map<Long, ShortCandle> candleStickMaxMap = new LinkedHashMap<Long, ShortCandle>();
    private Map<Long, ShortCandle> candleStickMinMap = new LinkedHashMap<Long, ShortCandle>();
    private TrendDirection direction = TrendDirection.UNDETERMINED;
    private final int nCandlesToCompare = 24;

    // This method loads the support and resistance points for a given pair.
    protected void loadSupResPoints() {
	// Reset list to free memory.
	candleStickMaxMap = new LinkedHashMap<Long, ShortCandle>();
	candleStickMinMap = new LinkedHashMap<Long, ShortCandle>();
	boolean firstSet = true;
	// Start with the last item and decrement.
	int indexOfLastItem = this.getCandleStickList().size() - 1;
	// Stop iterating when we reach 0 or 4x the number of candles to compare.
	int stopCondition = Math.max((indexOfLastItem - 4 * nCandlesToCompare), 0);
	ShortCandle pMin = null;
	ShortCandle pMax = null;
	// Decrement i by half of the number of candles to compare to ensure a x/2
	// distance between critical points.
	for (int i = indexOfLastItem; i > stopCondition; i = i - (nCandlesToCompare / 2)) {

	    // Iterates over the x candles at the time.
	    List<Candlestick> subList = this.getCandleStickList().subList(Math.max(i - nCandlesToCompare, 0), i);
	    ShortCandle pMinTemp = getMin(subList);
	    ShortCandle pMaxTemp = getMax(subList);
	    if (pMin == null || pMin.getLow() > pMinTemp.getLow()) {
		pMin = pMinTemp;
	    }
	    if (pMax == null || pMax.getHigh() < pMaxTemp.getHigh()) {
		pMax = pMaxTemp;
	    }

	    if (!firstSet) {
		// Add to list only after the second iteration to ensure a minimum spacing
		// between critical points.
		if (pMin != null) {
		    candleStickMinMap.put(pMin.getKey(), new ShortCandle(pMin));
		}

		if (pMax != null) {
		    candleStickMaxMap.put(pMax.getKey(), new ShortCandle(pMax));
		}
		pMin = null;
		pMax = null;
		firstSet = true;
	    } else {
		firstSet = false;
	    }
	}
	updateTrendDiretion();
    }

    private ShortCandle getMin(List<Candlestick> candleStickList) {
	ShortCandle minCandle = null;
	for (int i = 1; i < candleStickList.size() - 1; i++) {
	    double previous = Double.parseDouble(candleStickList.get(i - 1).getLow());
	    double current = Double.parseDouble(candleStickList.get(i).getLow());
	    double next = Double.parseDouble(candleStickList.get(i + 1).getLow());

	    if ((minCandle == null && current < previous && current < next)
		    || (minCandle != null && current < minCandle.getLow())) {
		minCandle = new ShortCandle(candleStickList.get(i));
	    }
	}
	return minCandle;
    }

    private ShortCandle getMax(List<Candlestick> candleStickList) {
	ShortCandle maxCandle = null;
	for (int i = 1; i < candleStickList.size() - 1; i++) {
	    double previous = Double.parseDouble(candleStickList.get(i - 1).getHigh());
	    double current = Double.parseDouble(candleStickList.get(i).getHigh());
	    double next = Double.parseDouble(candleStickList.get(i + 1).getHigh());
	    if ((maxCandle == null && current > previous && current > next)
		    || (maxCandle != null && current > maxCandle.getHigh())) {
		maxCandle = new ShortCandle(candleStickList.get(i));
	    }
	}
	return maxCandle;
    }

    private void updateTrendDiretion() {
	// Using the last few support points we can determine the direction that the
	// trend is going (up or Down). I chose to use the last 2 support points,
	// and the resistance to determine the direction.
	Double lastSupportPoint, beforeLastSupportPoint, resistance;
	lastSupportPoint = getLastSupportPoint();
	beforeLastSupportPoint = getBeforeLastSupportPoint();
	resistance = getLastResistancePoint();
	if (lastSupportPoint != null && beforeLastSupportPoint != null && resistance != null) {
	    // If (curPrice > resistance > (fSPoint and sSPoint(){then we have a break-up}
	    if (this.getClosePrice() > resistance && resistance > lastSupportPoint
		    && resistance > beforeLastSupportPoint) {
		direction = TrendDirection.BREAKUP;
	    }
	    // If (r1 < curPrice > fSPoint > sSPoint){then we have a higher min but no
	    // break-up}
	    else if (this.getClosePrice() > lastSupportPoint && lastSupportPoint > beforeLastSupportPoint) {
		direction = TrendDirection.HIGHERMIN;
	    }
	    // If (curPrice < fSPoint || sSPoint){then we have a break down}
	    else if (this.getClosePrice() < lastSupportPoint || this.getClosePrice() < beforeLastSupportPoint) {
		direction = TrendDirection.BREAKDOWN;
	    }
	}
    }

    public TrendDirection getTrendDirection() {
	return direction;
    }

    public boolean getBuySignal() {
	// Signal to buy when trend is moving up and the price is above the last high
	if (direction == TrendDirection.BREAKUP) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean getSellSignal() {
	// Signal to buy when trend is moving up and the price is above the last high
	if (direction != TrendDirection.BREAKUP && direction != TrendDirection.HIGHERMIN) {
	    return true;
	} else {
	    return false;
	}
    }

    public Double getLastSupportPoint() {
	List<ShortCandle> list = new ArrayList<ShortCandle>(candleStickMinMap.values());
	if (list.size() > 0) {
	    return list.get(0).getLow();
	} else {
	    return null;
	}
    }

    public Double getBeforeLastSupportPoint() {
	List<ShortCandle> list = new ArrayList<ShortCandle>(candleStickMinMap.values());
	if (list.size() > 1) {
	    return list.get(1).getLow();
	} else {
	    return null;
	}
    }

    public Double getLastResistancePoint() {
	List<ShortCandle> list = new ArrayList<ShortCandle>(candleStickMaxMap.values());
	if (list.size() > 0) {
	    return list.get(0).getHigh();
	} else {
	    return null;
	}
    }

    public String getLastSupportPoints() {
	ToStringBuilder str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE);
	for (ShortCandle candle : candleStickMinMap.values()) {
	    str.append("Val" + candle.getOpen(), candle.getLow());
	}
	return str.toString();
    }

    public String getLastResistancePoints() {
	ToStringBuilder str = new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE);
	for (ShortCandle candle : candleStickMaxMap.values()) {
	    str.append("Val" + candle.getOpen(), candle.getHigh());
	}
	return str.toString();
    }

    public String toString() {
	return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
		.append("\n\tClose Price", this.getClosePrice()).append("\n\tLastSupportPoint", getLastSupportPoint())
		.append("\n\tBeforeLastSupportPoint", getBeforeLastSupportPoint())
		.append("\n\tLastResitancePoint", getLastResistancePoint()).append("\n\tTrend", direction)
		.append("\n\tGetSellSignal", getSellSignal()).append("\n\tGetBuySignal", getBuySignal()).toString();
    }

    @Override
    public void update() {
	loadSupResPoints();
    }

}
