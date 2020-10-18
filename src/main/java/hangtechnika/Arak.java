package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Arak {

	//private Tools tools = new Tools();
	private Tools tools = Tools.tools();

	private String time;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private final ArrayList<ArrayList<String>> out = new ArrayList<>();

	public void convert() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {

		// Az out később a sr fejléc szerint lesz, jelenleg netsoft-os:
		out.add(new ArrayList<String>(Arrays.asList("Termék kód", "Nettó eladási egységár", "Beszerzési ár (Nettó)",
				"Termék típus", "Raktárkészlet")));

		//tools.hangzavarInit();

		kapottFileGetWorkingSheet(tools.fileName(".+_VK_Arlista\\.xlsx$"));
		voicekraftSpec();

		time = tools.now();
		toNetsoftArfrissites();
		toNetsoftArlista();
		toShoprenterKeszlet();
	}

	private void kapottFileGetWorkingSheet(String kapottFile)
			throws FileNotFoundException, IOException, InvalidFormatException, OpenXML4JException {
		ArrayList<ArrayList<String>> sheetNamesKapottInput;
		String sheetNameFromKapott;
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();
		sheetNamesKapottInput = new FromXLSX().read(kapottFile, kapottMap);
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);
	}

	private void voicekraftSpec() {
		// Előbb le kellene válogatni a kulcsokat --> residualAB
		// Voicekraft fejléc:
		// Cikkszám (0); Kategória név/nevek (1); Terméknév (hu) (2); Bruttó ár (3);
		// Nettó ár (4); Raktárkészlet (5)
		for (String key : sheetFromKapott.keySet()) {
			String termekKod = sheetFromKapott.get(key).get(0).replace(".0", "");
			if (termekKod.matches("\\d{2,}.+")) {
				if (tools.htKeys.contains(termekKod)) {
					String nettoEladasiEgysegar = tools.round(sheetFromKapott.get(key).get(4));
					String beszerzesiAr = tools
							.round(String.valueOf((Double.parseDouble(nettoEladasiEgysegar) * 0.75)));
					String raktarkeszlet = sheetFromKapott.get(key).get(5);
					out.add(new ArrayList<String>(Arrays.asList(termekKod, // Termék_kód
							nettoEladasiEgysegar, // Nettó eladási egységár
							beszerzesiAr, // Beszerzési ár (Nettó)
							"Termék", // Termék típus
							raktarkeszlet // Raktárkészlet
					)));
				} else {
					System.out.print(termekKod + "|");
					System.out.print(sheetFromKapott.get(key).get(1) + "|");
					System.out.print(sheetFromKapott.get(key).get(2) + "|");
					System.out.print(sheetFromKapott.get(key).get(3) + "|");
					System.out.print(sheetFromKapott.get(key).get(4) + "|");
					System.out.println(sheetFromKapott.get(key).get(5));
				}
			}
		}
		
//		for(String htKey : tools.htKeys) {
//			for (String kapottKey : sheetFromKapott.keySet()) {
//				if(htKey.equals(kapottKey)) {
//					//nothing
//				} 
//			}
//		}
	}

	public void toNetsoftArfrissites() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toCSVFile.add(row.get(0) + ";" + row.get(1).replace(".", ","));
		}
		tools.writeToFileCSV("netsoft_arfriss_" + time + ".csv", toCSVFile);
	}

	public void toNetsoftArlista() {
		LinkedHashMap<String, ArrayList<String>> toNetsoftArlista = new LinkedHashMap<>();
		for (ArrayList<String> row : out) {
			toNetsoftArlista.put(row.get(0), row);
		}
		ArrayList<String> toFile = new Arlistak().calculate(toNetsoftArlista);
		toFile.add(0, "Termék kód" + ";" + "Árlista" + ";" + "Egységár" + ";" + "Alapár (Nettó)" + ";" + "Árrés %" + ";"
				+ "Kedvezmény %");
		for (int i = 0; i < toFile.size(); i++) {
			toFile.set(i, toFile.get(i).replaceFirst("(;[^;]+){2}$", ""));//két utolsó oszlopot leveszi 
		}
		tools.writeToFileCSV("netsoft_arlistak_" + time + ".csv", toFile);
	}

	public void toShoprenterKeszlet() throws FileNotFoundException, IOException {
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenterMap = tools.prebuildToShoprenter();
		LinkedHashMap<String, ArrayList<String>> export = toShoprenterMap.get("export");
		for (ArrayList<String> row : out) {
			ArrayList<String> reducedRow = new ArrayList<>();
			reducedRow.add(row.get(0));
			reducedRow.add(row.get(4).replace("van", "Szerdára").replace("nincs", "Jelenleg nem érhető el!"));
			export.put(row.get(0), reducedRow);
		}
		export.remove("Termék kód");
		ToXLSX toxlsx = new ToXLSX();
		toxlsx.write(toShoprenterMap);
		toxlsx.writeout("../hangtechnika_files/shopr_keszl_" + time + ".xlsx");
	}

}