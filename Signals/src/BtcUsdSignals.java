import com.binance.api.client.domain.market.CandlestickInterval;

import Utils.Enums.TrendDirection;
import accountInfo.AccountInfo;

public class BtcUsdSignals {
	private static final String SYMBOL = "BTCUSDT";
	private static TASignals signals;
	private final CandlestickInterval interval = CandlestickInterval.HOURLY;
	
	public BtcUsdSignals() {
		super();
		BtcUsdSignals.signals = new TASignals(SYMBOL, interval);
	}
	
	public static TrendDirection getTrend() {
		Double curPrice = Double.parseDouble(AccountInfo.getRestClient().getPrice(SYMBOL).getPrice());
		return signals.getTrendDiretion(curPrice);
	}
	
	public static boolean isLiquidateToBtc() {
		TrendDirection trend = getTrend();
		return (trend.equals(TrendDirection.BREAKUP) || trend.equals(TrendDirection.HIGHERMIN));
	}
}
