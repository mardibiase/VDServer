package restHandlers;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import utils.FileResourceMapping;

@Path("/download")
public class DownloadManager {
		
	@Path("/fissi")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileFissi(){
		File file = new File(FileResourceMapping.avFissi_filePath);		
		return Response.ok(file).header("Content-Disposition", "attachment; filename=" + file.getName()).build();
	}
	
	@Path("/mobili")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileMobili(){
		File file = new File(FileResourceMapping.avMobili_filePath);		
		return Response.ok(file).header("Content-Disposition", "attachment; filename=" + file.getName()).build();
	}
	
	

}
