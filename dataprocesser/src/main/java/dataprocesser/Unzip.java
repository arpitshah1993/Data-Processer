package dataprocesser;
import org.apache.commons.io.FileUtils;
import net.lingala.zip4j.exception.ZipException;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.core.ZipFile;

public class Unzip {
	public static String unzip(String source){
	  
	    String destination = "Temp12344321";
	    deleteFolderIfExist(destination);
	    new File(destination).mkdir();
	    try {
	         ZipFile zipFile = new ZipFile(source);
	         if (zipFile.isEncrypted()) {
	        	 LoggerJava.log("File: "+source+" is encrypted.");
	         }
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	    	LoggerJava.log(e.getMessage());
	    }
	    return destination;
	}
	
	public static void deleteFolderIfExist(String path){
		try {
			FileUtils.deleteDirectory(new File(path));
		} 
		catch (IOException e) {
			LoggerJava.log(e.getMessage());
		}
	}
}
