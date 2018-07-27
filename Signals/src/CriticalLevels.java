import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.domain.market.Candlestick;

import Entities.ShortCandle;

public class CriticalLevels {

	ArrayList<ShortCandle> candleStickMaxList = new ArrayList<ShortCandle>();
	ArrayList<ShortCandle> candleStickMinList = new ArrayList<ShortCandle>();
	
	private void loadSupResPoints(int index, List<Candlestick>candleStickList) {
		// Iterates over all candles
		int indexOfLastItem = candleStickList.size() - 3;
		for (int i = index; i < indexOfLastItem;) {
			ShortCandle pMin = null;
			ShortCandle pMax = null;
			// Iterates over the 10 previous candles
			int maxIndex = Math.min(indexOfLastItem, i + 10);
			for (int c = i; c < maxIndex; c++) {
				ShortCandle p_previous = new ShortCandle(candleStickList.get(c - 2));
				ShortCandle previous = new ShortCandle(candleStickList.get(c - 1));
				ShortCandle current = new ShortCandle(candleStickList.get(c));
				ShortCandle next = new ShortCandle(candleStickList.get(c + 1));
				ShortCandle n_next = new ShortCandle(candleStickList.get(c + 2));
				// If (previous < current > next && current > previousMax){maximum found}
				if (current.getHigh() > next.getHigh() && current.getHigh() > previous.getHigh()
						&& (pMax == null || current.getHigh() >= pMax.getHigh())) {
					pMax = current;
				}
				// If (previous > current < next && current < previousMin){minimum found}
				if (current.getLow() < next.getLow() && current.getLow() < previous.getLow()
						&& (pMin == null || current.getLow() < pMin.getLow())) {
					pMin = current;
				}
			}
			i = i + 9;
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
}
