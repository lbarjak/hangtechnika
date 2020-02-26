package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Hangtechnika {

	public static void main(String[] args)
			throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {
		
		//Ide gyártó osztály kell ami egykét ad vissza: new Tools().hangzavarInit();
		// new FBT().convert();
		new Voicekraft().convert();
	}

}
