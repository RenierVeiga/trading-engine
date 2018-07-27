import AccountInfo.AccountManager;

public class MainClass {

	public static void createThread(String assetA, String assetB, double quantityP) {
		final Thread one = new Thread() {
			@Override
			public void run() {
					new OrderManager(assetA, assetB, quantityP);
			}
		};
		one.start();
	}

	public static void main(String[] args) throws InterruptedException {

//		createThread("btc", "usdt", 1);
//		createThread("ICX", "BTC", 1);
//		createThread("NULS", "BTC", 1);
//		createThread("IOST", "BTC", 1);
		
		AccountManager acm = new AccountManager();
		System.out.println(AccountManager.getRestClient().getExchangeInfo().getSymbols().toString());
		System.out.println(acm.getAccountBalanceCache().toString());
	}

}
