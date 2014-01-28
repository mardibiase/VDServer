package fileHandlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import utils.FileResourceMapping;

public class UpdateCSVMobili {

	public static void doUpdate(Double latit, Double longit) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(FileResourceMapping.avMobili_filePath));
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
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FileResourceMapping.avMobili_filePath, true)));
			out.print(longit + "," + latit + "," + "user_added" + "\n");
			out.close();
		}			

	}
}
