package connection;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.connect.AccountInfo;

public class HeartBeatThread extends Thread {
	private BinanceApiAsyncRestClient restAsyncClient = AccountInfo.getRestAsyncClient();

	public HeartBeatThread() {
	}

	public void run() {
		keepAliveHeartBeat();
	}

	public void keepAliveHeartBeat() {
		try {
			// Sleep for 30 minutes.
			Thread.sleep(1800000);
			// Sleep for 5 minutes.
			// Thread.sleep(300000);
			// Send heart beat.
			restAsyncClient.keepAliveUserDataStream(AccountInfo.getListenKey(), response -> {
			});
			// Do it all over again.
			keepAliveHeartBeat();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("Date", new Date().toString()).toString();
	}
}
