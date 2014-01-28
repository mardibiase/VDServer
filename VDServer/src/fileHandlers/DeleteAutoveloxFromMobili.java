package fileHandlers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import utils.FileResourceMapping;

public class DeleteAutoveloxFromMobili {

	/**
	 * Sets up the processors used for the file. Returns the processor.
	 */
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new ParseDouble(), // latit
				new ParseDouble(), // longit
				new NotNull() // tutto il resto contenuto nel file, dopo longit e latit
		};
		return processors;
	}

	/**
	 * La doDelete cancella la entry specifica dal file Autovelox_Mobili.csv Il file Autovelox_Mobili.csv è fatto in questo modo:
	 * <longitudine>;<latitudine>;<altro_testo>\n
	 */
	public static void doDelete(Double latit, Double longit) throws IOException {
		ICsvListReader listReader = null;
		ICsvListWriter listWriter = null;

		try {
			listReader = new CsvListReader(new FileReader(FileResourceMapping.avMobili_filePath), CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] processors = getProcessors();
			List<Object> row; // generica row letta da file
			List<Object> rowToDelete = null; // row dove salviamo la reference da eliminare
			List<List<Object>> tempArray = new ArrayList<List<Object>>(); // arraylist di tutte le row da rimettere nel file
			while ((row = listReader.read(processors)) != null) {
				// se la riga contiene longit e latit passati, dobbiamo cancellarla
				if (row.contains(longit) && row.contains(latit)) {
					rowToDelete = row;
				}
			}
			// rileggiamo tutto il file
			listReader = new CsvListReader(new FileReader(FileResourceMapping.avMobili_filePath), CsvPreference.STANDARD_PREFERENCE);
			// check se rowToDelete è stata riempita
			if (rowToDelete != null) {
				while ((row = listReader.read(processors)) != null) {
					// tutte le righe standard che non ci interessano passano sul file nuovo as-is
					// aggiungili ad una collezione di oggetti temporanea
					if (!rowToDelete.toString().equals(row.toString())) {
						List<Object> line = Arrays.asList(new Object[] { row.get(0).toString(), row.get(1).toString(), row.get(2).toString() });
						tempArray.add(line);
					}
				}
				// dalla collezione temp riscrivili su file
				listWriter = new CsvListWriter(new FileWriter(FileResourceMapping.avMobili_filePath), CsvPreference.STANDARD_PREFERENCE);
				for (int i = 0; i < tempArray.size(); i++) {
					List<Object> lineToCopy = tempArray.get(i);
					List<Object> Row = Arrays.asList(new Object[] { lineToCopy.get(0).toString(), lineToCopy.get(1).toString(), lineToCopy.get(2).toString() });
					listWriter.write(Row, processors);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		if (listReader != null) {
			listReader.close();
		}
		if (listWriter != null) {
			listWriter.close();
		}

	}
}
