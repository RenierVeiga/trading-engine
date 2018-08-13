package connection;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;

import subjects.AccountBalanceStreamSubject;

/**
 * 
 * @author Renier Veiga
 * 
 *         Date: Aug 12, 2018
 * 
 *         Creates a listenKey and a heart beat thread. Kicks off the engine
 *         while sending heart beats every 30 minutes and refreshing the
 *         listenKey every 12 hours.
 * 
 */
public class ConnectionManager {

	private static HeartBeatThread heartBeat = new HeartBeatThread();
	private static ListenKeyThread listenKey = new ListenKeyThread();

	public static void init() {
		heartBeat.start();
		listenKey.start();
		AccountBalanceStreamSubject.initBalanceStream();
	}

	public static void resetListenKeyConnection() {
		AccountBalanceStreamSubject.initBalanceStream();
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("Date", new Date().toString()).toString();
	}
}
