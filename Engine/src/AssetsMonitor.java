
import java.util.Set;

import AccountInfo.AccountInfo;

/**
 * @author Renier Veiga
 * 
 *         Date: Jul 28, 2018
 * 
 *         For each asset create a tread to watch each asset and liquidate it if
 *         necessary.
 *
 */

public class AssetsMonitor {

	AccountInfo acm = new AccountInfo();
	Set<String> assets = acm.getAssets();

	public AssetsMonitor() {
		for (String asset : assets) {
			createThread(asset);
		}
	}

	private static void createThread(String asset) {
		final Thread one = new Thread() {
			@Override
			public void run() {
				new AssetWatcher(asset);
			}
		};
		one.start();
	}
}
