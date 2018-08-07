import reports.Report;
import subjects.AccountBalanceStreamSubject;

public class MainClass {

	public static void main(String[] args) throws InterruptedException {
		try {
			AccountBalanceStreamSubject.getInstance().start();
		} catch (Exception e) {
			Report.createReport(e.getMessage());
			Thread.sleep(1000);
			AccountBalanceStreamSubject.getInstance().start();
		}

	}
}