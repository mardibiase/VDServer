package sources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/feedback")
public class FeedbackAutovelox {

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public  @QueryParam("result") String feedbackAutovelox(@FormParam("latit") String latit, @FormParam("longit") String longit){
		String toRet = "";
		try{
			UpdateFeedback.doUpdate(Double.parseDouble(latit),Double.parseDouble(longit));
			toRet = "OK";
		}
		catch(Exception e){
			toRet = "FAILED";
		}
		return toRet;		
	}
}
