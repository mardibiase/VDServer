package sources;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import sources.UpdateCSV.ContainerPaths;

@Path("/deleteAV")
public class DeleteAutovelox {

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public @QueryParam("result")
	String deleteAutovelox(@FormParam("latit") String latit, @FormParam("longit") String longit) {
		String toRet = "";
		try {
			// prima di tutto dobbiamo aggiornare il valore di #delete nel file Feedback_Autovelox.csv
			// la funzione DeleteAutovelox.UpdateDelete restituisce un booleano che indica se il valore aggiornato
			// del #delete è maggiore dei #feedback
			boolean isToDelete = DeleteAutovelox.UpdateDelete(Double.parseDouble(latit), Double.parseDouble(longit));
			if (isToDelete) {
				DeleteAutoveloxFromFeedback.doDelete(Double.parseDouble(latit), Double.parseDouble(longit));
				DeleteAutoveloxFromMobili.doDelete(Double.parseDouble(latit), Double.parseDouble(longit));
			}
			toRet = "OK";
		} catch (Exception e) {
			toRet = "FAILED";
		}
		return toRet;
	}

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
	private static boolean UpdateDelete(Double latit, Double longit) throws Exception {
		boolean result = false;
		ICsvListReader listReader = null;
		ICsvListWriter listWriter = null;
		try {
			listReader = new CsvListReader(new FileReader(ContainerPaths.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
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
			listReader = new CsvListReader(new FileReader(ContainerPaths.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
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
				listWriter = new CsvListWriter(new FileWriter(ContainerPaths.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
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
				listWriter = new CsvListWriter(new FileWriter(ContainerPaths.feedback_filePath), CsvPreference.STANDARD_PREFERENCE);
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
}
