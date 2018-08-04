package Entities;

import com.binance.api.client.domain.market.Candlestick;

public class ShortCandle {
	Long key;
	double low, high, open, close;

	public ShortCandle(Candlestick candle) {
		key = candle.getOpenTime();
		low = Double.parseDouble(candle.getLow());
		high = Double.parseDouble(candle.getHigh());
		open = Double.parseDouble(candle.getOpen());
		close = Double.parseDouble(candle.getClose());
	}

	public ShortCandle(ShortCandle candle) {
		key = candle.getKey();
		low = candle.getLow();
		high = candle.getHigh();
		open = candle.getOpen();
		close = candle.getClose();
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

}
