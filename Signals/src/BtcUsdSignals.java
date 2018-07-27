import com.binance.api.client.domain.market.CandlestickInterval;

import AccountInfo.AccountManager;
import Utils.Enums.TrendDirection;

public class BtcUsdSignals {
	private final String SYMBOL = "BTCUSDT";
	private TASignals signals;
	private final CandlestickInterval interval = CandlestickInterval.HOURLY;
	
	public BtcUsdSignals() {
		super();
		this.signals = new TASignals(SYMBOL, interval);
	}
	
	public TrendDirection getTrend() {
		Double curPrice = Double.parseDouble(AccountManager.getRestClient().getPrice(SYMBOL).getPrice());
		return signals.getTrendDiretion(curPrice);
	}
}
