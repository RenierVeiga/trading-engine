package AccountInfo;

import java.util.Map;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;
import properties.Properties;

public class AccountManager {

	// API initialization
	private static final BinanceApiClientFactory factory = BinanceApiClientFactory
			.newInstance(Properties.getInstance().getKey(), Properties.getInstance().getSecret());
	
	private static final BinanceApiAsyncRestClient restAsynClient = factory.newAsyncRestClient();
	private static final BinanceApiRestClient restClient = factory.newRestClient();
	private static final BinanceApiWebSocketClient socketClient = factory.newWebSocketClient();
	
	// Account Information 
	private Map<String, AssetBalance> accountBalanceCache;

	// Listen key used to interact with the user data streaming API.
	private final String listenKey;

	public AccountManager() {
		this.listenKey = initializeAssetBalanceCacheAndStreamSession();
		startAccountBalanceEventStreaming(listenKey);
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

	// Add information about account balances for each asset.
	/**
	 * @return an account balance cache, containing the balance for every asset in
	 *         this account.
	 */
	public Map<String, AssetBalance> getAccountBalanceCache() {
		return accountBalanceCache;
	}

	/**
	 * Initializes the asset balance cache by using the REST API and starts a new
	 * user data streaming session.
	 *
	 * @return a listenKey that can be used with the user data streaming API.
	 */
	private String initializeAssetBalanceCacheAndStreamSession() {

		Account account = restClient.getAccount();

		this.accountBalanceCache = new TreeMap<>();
		for (AssetBalance assetBalance : account.getBalances()) {
			if(Float.parseFloat(assetBalance.getFree())>0) {
			accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
			}
		}

		return restClient.startUserDataStream();
	}

	public void setAccountBalanceCache(Map<String, AssetBalance> accountBalanceCache) {
		this.accountBalanceCache = accountBalanceCache;
	}

	/**
	 * Begins streaming of agg trades events.
	 */
	private void startAccountBalanceEventStreaming(String listenKey) {

		socketClient.onUserDataUpdateEvent(listenKey, response -> {
			if (response.getEventType() == UserDataUpdateEventType.ACCOUNT_UPDATE) {
				// Override cached asset balances
				for (AssetBalance assetBalance : response.getAccountUpdateEvent().getBalances()) {
					accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
				}
				System.out.println(accountBalanceCache);
			}
		});
	}
	
	// TODO - Add information about amount to trade for each asset.
}
