package hangtechnika;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Tools {

	LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> hangzavarMap = new LinkedHashMap<>();
	Set<String> htKeys;

	public String now() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmm");
		String now = formatter.format(today);
		return now;
	}

	public void diff(Date start, Date stop) {
		long diffSec = (stop.getTime() - start.getTime()) / 1000;
		System.out.println("--------------------");
		System.out.println(diffSec / 60 + " perc " + diffSec % 60 + " másodperc");
	}

	public void writeToFileCSV(String nameOfFile, ArrayList<String> toCSVFile) {

		String time = now();
		FileWriter fw;
		try {
			fw = new FileWriter(nameOfFile + time + ".csv");
			for (String row : toCSVFile) {
				fw.write(row + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> prebuildToShoprenter() {
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenterMap = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> export = new LinkedHashMap<>();
		toShoprenterMap.put("export", export);
		toShoprenterMap.get("export").put("Cikkszám",
				new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));
		LinkedHashMap<String, ArrayList<String>> columns = new LinkedHashMap<>();
		toShoprenterMap.put("columns", columns);
		toShoprenterMap.get("columns").put("sku", new ArrayList<>(Arrays.asList("sku", "stockStatusName")));
		toShoprenterMap.get("columns").put("Cikkszám",
				new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));
		return toShoprenterMap;
	}

	public void hangzavarInit() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {
		String xlsxName = "hangzavar-xlsx-export-2020-01-12_22_51_47.xlsx";
		new FromXLSX().read(xlsxName, hangzavarMap);
		htKeys = hangzavarMap.get("export").keySet();
	}

	public String rounding(String numberSring) {
		Integer num = (int) (100 * Double.parseDouble(numberSring));
		Double number = (double) (num / 100);
		numberSring = String.valueOf(number);
		return numberSring;
	}

}
