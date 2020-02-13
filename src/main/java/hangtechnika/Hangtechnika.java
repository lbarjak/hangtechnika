package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Hangtechnika {
	
	public static void main(String[] args)
			throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {
		//new FBT().convert();
		new Voicekraft().convert();
	}
	
}
