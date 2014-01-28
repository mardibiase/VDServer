package fileHandlers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import utils.FileResourceMapping;

public class UpdateFeedback {

	/**
	 * Sets up the processors used for the file. Returns the processor.
	 */
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new ParseDouble(), // latit
				new ParseDouble(), // longit
				new ParseInt(), // feedback
				new ParseInt() // delete
		};
		return processors;
	}

	/**
	 * Questo metodo aggiorna il valore di #delete all'interno di Feedback_Autovelox.csv Ritorna true se il valore aggiornato di #delete > #feedback
	 * 
	 * @param latit
	 * @param longit
	 * @return
	 */
	public static boolean UpdateDelete(Double latit, Double longit) throws Exception {
		boolean result = false;
		ICsvListReader listReader = null;
		ICsvListWriter listWriter = null;
		try {
			listReader = new CsvListReader(new FileReader(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
			final CellProcessor[] processors = getProcessors();
			List<Object> row; // generica row letta da file
			List<Object> rowToUpdate = null; // row dove salviamo la reference da aggiornare
			List<List<Object>> tempArray = new ArrayList<List<Object>>(); // arraylist di tutte le row da rimettere nel file
			while ((row = listReader.read(processors)) != null) {
				// se la riga row contiene longit e latit passati, è quella da aggiornare
				if (row.contains(longit) && row.contains(latit)) {
					rowToUpdate = row;
				}
			}
			listReader = new CsvListReader(new FileReader(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
			// check se rowToUpdate è stata riempita
			if (rowToUpdate != null) {
				// rileggi il file
				while ((row = listReader.read(processors)) != null) {
					// tutte le righe standard che non ci interessano passano sul file nuovo as-is
					// aggiungili ad una collezione di oggetti temporanea
					if (!rowToUpdate.toString().equals(row.toString())) {
						List<Object> line = Arrays.asList(new Object[] { row.get(0).toString(), row.get(1).toString(), row.get(2).toString(),
								row.get(3).toString() });
						tempArray.add(line);
					}
				}
				// dalla collezione temp riscrivili su file
				listWriter = new CsvListWriter(new FileWriter(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
				for (int i = 0; i < tempArray.size(); i++) {
					List<Object> lineToCopy = tempArray.get(i);
					List<Object> Row = Arrays.asList(new Object[] { lineToCopy.get(0).toString(), lineToCopy.get(1).toString(), lineToCopy.get(2).toString(),
							lineToCopy.get(3).toString() });
					listWriter.write(Row, processors);
				}
				// preleva il numero dei feedback dalla row da aggiornare
				Integer numberFeedback = Integer.parseInt(rowToUpdate.get(2).toString());
				// aggiorna il numero dei delete
				Integer numberDelete = Integer.parseInt(rowToUpdate.get(3).toString()) + 1;
				// aggiorna la row su file con il nuovo numero di delete
				List<Object> updatedLine = Arrays.asList(new Object[] { rowToUpdate.get(0).toString(), rowToUpdate.get(1).toString(), numberFeedback,
						numberDelete });
				listWriter.write(updatedLine, processors);

				// check se il valore nuovo di #delete > #feedback
				if (numberDelete > numberFeedback)
					result = true;

			} else {
				// dobbiamo aggiungere la riga al file Feedback_Autovelox.csv
				// e porre feedback = 1, delete = 1;

				// leggi tutto il file e aggiungi le righe alla collection
				while ((row = listReader.read(processors)) != null) {
					List<Object> line = Arrays
							.asList(new Object[] { row.get(0).toString(), row.get(1).toString(), row.get(2).toString(), row.get(3).toString() });
					tempArray.add(line);
				}
				// dalla collezione temp riscrivili su file
				listWriter = new CsvListWriter(new FileWriter(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
				for (int i = 0; i < tempArray.size(); i++) {
					List<Object> lineToCopy = tempArray.get(i);
					List<Object> Row = Arrays.asList(new Object[] { lineToCopy.get(0).toString(), lineToCopy.get(1).toString(), lineToCopy.get(2).toString(),
							lineToCopy.get(3).toString() });
					listWriter.write(Row, processors);
				}
				// infine aggiungi la riga di interesse
				List<Object> newLine = Arrays.asList(new Object[] { latit.toString(), longit.toString(), 1, 1 });
				listWriter.write(newLine, processors);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
		}
		if (listReader != null) {
			listReader.close();
		}
		if (listWriter != null) {
			listWriter.close();
		}

		return result;
	}
	
	public static void doUpdate(Double latit, Double longit) throws IOException {
		/**
		 * Il file Feedback_Autovelox.csv è fatto in questo modo: <longitudine>;<latitudine>;#feedback;#delete\n
		 */

		ICsvListReader listReader = null;
		ICsvListWriter listWriter = null;
		try {
			listReader = new CsvListReader(new FileReader(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] processors = getProcessors();
			List<Object> row; // generica row letta da file
			List<Object> rowToEdit = null; // row dove salviamo la reference da editare
			List<List<Object>> tempArray = new ArrayList<List<Object>>(); // arraylist di tutte le row da rimettere nel file
			while ((row = listReader.read(processors)) != null) {
				// se la riga row contiene longit e latit passati, è quella da editare
				if (row.contains(longit) && row.contains(latit)) {
					rowToEdit = row;
				}
			}

			listReader = new CsvListReader(new FileReader(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
			// check se rowToEdit è stata riempita (ovvero not null)
			if (rowToEdit != null) {
				// rileggi il file
				while ((row = listReader.read(processors)) != null) {
					// tutte le righe standard che non ci interessano passano sul file nuovo as-is
					// aggiungili ad una collezione di oggetti temporanea
					if (!rowToEdit.toString().equals(row.toString())) {
						List<Object> line = Arrays.asList(new Object[] { row.get(0).toString(), row.get(1).toString(), row.get(2).toString(),
								row.get(3).toString() });
						tempArray.add(line);
					}
				}
				// dalla collezione temp riscrivili su file
				listWriter = new CsvListWriter(new FileWriter(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
				for (int i = 0; i < tempArray.size(); i++) {
					List<Object> lineToCopy = tempArray.get(i);
					List<Object> Row = Arrays.asList(new Object[] { lineToCopy.get(0).toString(), lineToCopy.get(1).toString(), lineToCopy.get(2).toString(),
							lineToCopy.get(3).toString() });
					listWriter.write(Row, processors);

				}
				// infine aggiorna la riga di interesse
				List<Object> updatedLine = Arrays.asList(new Object[] { rowToEdit.get(0).toString(), rowToEdit.get(1).toString(),
						Integer.parseInt(rowToEdit.get(2).toString()) + 1, rowToEdit.get(3).toString() });
				listWriter.write(updatedLine, processors);
			}
			else{
				/*
				 * aggiunge una nuova entry all'interno del file con il campo feedback = 2
				 * poichè si presuppone che se un file è all'interno degli autovelox, abbia già feedback = 1
				 */
				//leggi tutto il file e aggiungi le righe alla collection
				while ((row = listReader.read(processors)) != null) {
					List<Object> line = Arrays.asList(new Object[] { row.get(0).toString(), row.get(1).toString(), row.get(2).toString(),
							row.get(3).toString() });
					tempArray.add(line);
				}
				// dalla collezione temp riscrivili su file
				listWriter = new CsvListWriter(new FileWriter(FileResourceMapping.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
				for (int i = 0; i < tempArray.size(); i++) {
					List<Object> lineToCopy = tempArray.get(i);
					List<Object> Row = Arrays.asList(new Object[] { lineToCopy.get(0).toString(), lineToCopy.get(1).toString(), lineToCopy.get(2).toString(),
							lineToCopy.get(3).toString() });
					listWriter.write(Row, processors);
				}
				// infine aggiungi la riga di interesse
				List<Object> newLine = Arrays.asList(new Object[] { longit.toString(), latit.toString(), 2, 0 });
				listWriter.write(newLine, processors);
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
