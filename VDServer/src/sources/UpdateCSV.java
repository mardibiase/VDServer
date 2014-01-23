package sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class UpdateCSV {

	public static void doUpdate(Double latit, Double longit) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(ContainerPaths.avMobili_filePath));
		String line = "";
		boolean isPresent = false;
		while ((line = br.readLine()) != null) {
			if (line.contains(latit.toString()) && line.contains(longit.toString())) {
				isPresent = true;
				System.out.println("Autovelox longit: " + longit + " latit: " + latit + " found. Not added to the file.");
			}
		}
		br.close();
		if (!isPresent) {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ContainerPaths.avMobili_filePath, true)));
			out.print(longit + "," + latit + "," + "user_added" + "\n");
			out.close();
		}
						

	}

	//inner class just to save somewhere the static references to the files used by the server
	public class ContainerPaths {

		//user defined based on whether the file are saved
		private static final String filePath = "C:\\Users\\Caruso\\workspace\\VDServer\\files\\";
		
		//These values below should not be modified
		private static final String avFissi_fileName = "Autovelox_Fissi.csv";
		private static final String avMobili_fileName = "Autovelox_Mobili.csv";
		private static final String feedback_fileName = "Feedback_Autovelox.csv";
		private static final String users_fileName = "users.xml";
		
		public static final String avFissi_filePath = filePath + avFissi_fileName;
		public static final String avMobili_filePath = filePath + avMobili_fileName;
		public static final String feedback_filePath = filePath + feedback_fileName;
		public static final String users_filePath = filePath + users_fileName;
		
		
	}
}
