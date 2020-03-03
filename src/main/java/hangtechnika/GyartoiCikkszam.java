package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class GyartoiCikkszam implements GlobalVariables {
	
	LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> gyartoiCikkszamMap = new LinkedHashMap<>();
	String fileName = "../hangtechnika_files/RØDE_árlista+VE átadási árak_2020_03-mod.xlsx";
	LinkedHashMap<String,ArrayList<String>> htWork = HANGZAVAR_MAP.get("export");
	LinkedHashMap<String,ArrayList<String>> gySheet;
	
	void insertCikkszam() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {
		ArrayList<ArrayList<String>> sheetNamesAndFirstElement = new FromXLSX().read(fileName, gyartoiCikkszamMap);
		String sheetName = sheetNamesAndFirstElement.get(0).get(0);
		gySheet = gyartoiCikkszamMap.get(sheetName);
		String firstElement = sheetNamesAndFirstElement.get(1).get(0);
		int indexOfCikkszam = gySheet.get(firstElement).indexOf("Cikkszám");
		for(String gyartoiCikkszam : gySheet.keySet()) {
			for(String cikkszam : htWork.keySet()) {
				if(htWork.get(cikkszam).contains(gyartoiCikkszam)) {
					gySheet.get(gyartoiCikkszam).set(indexOfCikkszam, cikkszam);
					System.out.println(gySheet.get(gyartoiCikkszam));
				}
			}
		}
	}

}