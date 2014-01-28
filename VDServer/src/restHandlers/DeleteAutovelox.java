package restHandlers;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fileHandlers.DeleteAutoveloxFromFeedback;
import fileHandlers.DeleteAutoveloxFromMobili;
import fileHandlers.UpdateFeedback;

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
			boolean isToDelete = UpdateFeedback.UpdateDelete(Double.parseDouble(latit), Double.parseDouble(longit));
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
}