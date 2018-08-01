package subjects;

import java.util.Map;
import java.util.TreeMap;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;
import com.binance.api.connect.AccountInfo;

import observers.AccountBalanceObserver;

/**
 * @author Renier Veiga
 * 
 *         Date: Jul 28, 2018
 * 
 *         Stream the account balance and updates the list of assets on each
 *         trade event. Kicks off a asset watch for each asset with a balance.
 *
 */

public class AccountBalanceStreamSubject {

	private static Map<String, AssetBalance> accountBalanceCache;
	private static AccountBalanceObserver observer = AccountBalanceObserver.getInstance();
	private static AccountBalanceStreamSubject instance = new AccountBalanceStreamSubject();

	private AccountBalanceStreamSubject() {

	}

	public void start() {
		System.out.println("Started Account Balance Stream.");
		// Start balance streaming
		String listenKey = initializeAssetBalanceCacheAndStreamSession();
		startAccountBalanceEventStreaming(listenKey);
	}

	/**
	 * Initializes the asset balance cache by using the REST API and starts a new
	 * user data streaming session.
	 *
	 * @return a listenKey that can be used with the user data streaming API.
	 */
	private String initializeAssetBalanceCacheAndStreamSession() {

		Account account = AccountInfo.getRestClient().getAccount();

		accountBalanceCache = new TreeMap<>();
		for (AssetBalance assetBalance : account.getBalances()) {
			if (Double.parseDouble(assetBalance.getFree()) > 0.1) {
				System.out.println(assetBalance.toString());
				accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
			}
		}
		updateObservers();
		return AccountInfo.getRestClient().startUserDataStream();
	}

	/**
	 * Begins streaming of agg trades events.
	 */
	private void startAccountBalanceEventStreaming(String listenKey) {

		AccountInfo.getSocketClient().onUserDataUpdateEvent(listenKey, response -> {
			if (response.getEventType() == UserDataUpdateEventType.ACCOUNT_UPDATE) {
				// Override cached asset balances
				for (AssetBalance assetBalance : response.getAccountUpdateEvent().getBalances()) {
					accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
				}
				// Update observers subscribed to this message.
				updateObservers();
			}
		});
	}

	public Map<String, AssetBalance> getAccountBalanceCache() {
		return accountBalanceCache;
	}

	public void updateObservers() {
		System.out.println("Account Balance Update.");
		observer.update(accountBalanceCache.keySet());
	}

	public static AccountBalanceStreamSubject getInstance() {
		return instance;
	}
}
