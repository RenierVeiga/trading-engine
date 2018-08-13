package com.binance.api.connect;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;

import properties.Properties;

public class AccountInfo {

	// API initialization
	private static final BinanceApiClientFactory factory = BinanceApiClientFactory
			.newInstance(Properties.getInstance().getKey(), Properties.getInstance().getSecret());

	private static final BinanceApiAsyncRestClient restAsynClient = factory.newAsyncRestClient();
	private static final BinanceApiRestClient restClient = factory.newRestClient();
	private static final BinanceApiWebSocketClient socketClient = factory.newWebSocketClient();
	private static String listenKey = AccountInfo.getRestClient().startUserDataStream();

	public AccountInfo() {

	}

	public static BinanceApiRestClient getRestClient() {
		return restClient;
	}

	public static BinanceApiAsyncRestClient getRestAsyncClient() {
		return restAsynClient;
	}

	public static BinanceApiWebSocketClient getSocketClient() {
		return socketClient;
	}

	public static String getListenKey() {
		return listenKey;
	}

	public static void setListenKey(String listenKey) {
		AccountInfo.listenKey = listenKey;
	}

}
