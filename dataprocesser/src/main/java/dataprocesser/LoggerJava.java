package dataprocesser;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerJava {
	private final static Logger LOGGER = Logger.getLogger("MyLog");

    public static void log(String message) {
    	LOGGER.setLevel(Level.INFO);
        LOGGER.info(message);
    }
	public static void configLogger() {
		FileHandler fh;
		try {
			fh = new FileHandler("MyLogFile.log");
			LOGGER.addHandler(fh);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
