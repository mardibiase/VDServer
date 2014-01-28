package utils;

//class just to save somewhere the static references to the files used by the server
public class FileResourceMapping {

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
