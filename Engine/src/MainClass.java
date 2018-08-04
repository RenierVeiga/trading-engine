import subjects.AccountBalanceStreamSubject;

public class MainClass {

	public static void main(String[] args) throws InterruptedException {

		AccountBalanceStreamSubject.getInstance().start();
	}

}
