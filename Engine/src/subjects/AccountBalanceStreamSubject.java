package subjects;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;
import com.binance.api.connect.AccountInfo;

import observers.AccountBalanceObserver;
import reports.Report;

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
	private static Closeable socketStream;

	private AccountBalanceStreamSubject() {
	}

	public static void initBalanceStream() {
		// Resets the socket connection if previously started.
		resetConnection();
		// Clear and initialize cache.
		initializeAssetBalanceCacheAndStreamSession();
		// Start balance streaming.
		startAccountBalanceEventStreaming();
	}

	/**
	 * Initializes the asset balance cache by using the REST API and starts a new
	 * user data streaming session.
	 *
	 * @return a listenKey that can be used with the user data streaming API.
	 */
	public static void initializeAssetBalanceCacheAndStreamSession() {

		Account account = AccountInfo.getRestClient().getAccount();

		accountBalanceCache = new TreeMap<>();
		for (AssetBalance assetBalance : account.getBalances()) {
			if (Double.parseDouble(assetBalance.getFree()) > 0) {
				System.out.println(assetBalance.toString());
				accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
			}
		}
		updateObservers();
	}

	/**
	 * Begins streaming of agg trades events.
	 */
	private static void startAccountBalanceEventStreaming() {
		BinanceApiWebSocketClient client = AccountInfo.getSocketClient();
		socketStream = client.onUserDataUpdateEvent(AccountInfo.getListenKey(), response -> {
			if (response.getEventType() == UserDataUpdateEventType.ACCOUNT_UPDATE) {
				// Override cached asset balances
				for (AssetBalance assetBalance : response.getAccountUpdateEvent().getBalances()) {
					if (Double.parseDouble(assetBalance.getFree()) > 0) {
						accountBalanceCache.put(assetBalance.getAsset(), assetBalance);
					}
				}
				// Update observers subscribed to this message.
				updateObservers();
			}
		});
	}

	public synchronized Map<String, AssetBalance> getAccountBalanceCache() {
		return accountBalanceCache;
	}

	public static void updateObservers() {
		observer.update(accountBalanceCache);
	}

	public static AccountBalanceStreamSubject getInstance() {
		return instance;
	}

	public static void resetConnection() {
		try {
			if (socketStream != null) {
				// Stops listening to the socket stream.
				socketStream.close();
			}
		} catch (IOException e) {
			Report.createReport("Failed to close balance socket stream. " + e.getMessage());
			e.printStackTrace();
		}
	}
}
