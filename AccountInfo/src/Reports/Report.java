package Reports;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Report {
	private static final String FILENAME = "C:\\Users\\renie\\Desktop\\report.txt";

	public static void createReport(String content) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {

			fw = new FileWriter(FILENAME, true);
			bw = new BufferedWriter(fw);
			bw.write(content);

			System.out.println("Done");

		} catch (final IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null) {
					bw.close();
				}

				if (fw != null) {
					fw.close();
				}

			} catch (final IOException ex) {

				ex.printStackTrace();

			}

		}
	}

	public Report() {
		// TODO Auto-generated constructor stub
	}
}
