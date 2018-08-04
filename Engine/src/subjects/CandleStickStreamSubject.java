package subjects;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.examples.CandleSticksCache;

import observers.AssetObserver;

/**
 * @author Renier Veiga
 * 
 *         Date: Jul 28, 2018
 * 
 *         Stream the account balance and updates the list of assets on each
 *         trade event. Kicks off a asset watch for each asset with a balance.
 *
 */

public class CandleStickStreamSubject {

	private String symbol, assetA;
	private CandleSticksCacheImpl candlesticksCache;
	private AssetObserver assetObserver;

	public CandleStickStreamSubject(String assetA, String assetB) {
		this.assetA = assetA;
		symbol = String.format("%s%s", assetA, assetB).toUpperCase();
		candlesticksCache = new CandleSticksCacheImpl(symbol, CandlestickInterval.FOUR_HOURLY);
		assetObserver = new AssetObserver(assetA, assetB, candlesticksCache);
	}

	public class CandleSticksCacheImpl extends CandleSticksCache {

		public CandleSticksCacheImpl(String symbol, CandlestickInterval interval) {
			super(symbol, interval);
		}

		@Override
		public void onCandleStickEvent() {
			assetObserver.update(this);
		}
	}

	public void unregister() {
		// Clear the object and let the garbage collector release resources.
		candlesticksCache = null;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getAssetA() {
		return assetA;
	}

}