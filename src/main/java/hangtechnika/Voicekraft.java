package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Voicekraft {

	private Tools tools = new Tools();

	private ArrayList<ArrayList<String>> sheetNamesKapottInput;
	private String sheetNameFromKapott;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private final String kapottFile = "bb11a3a45af20fe453cca9a783effd05_VK_Arlista.xlsx";
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();
	private final ArrayList<ArrayList<String>> out = new ArrayList<>();

	public void convert() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {

		sheetNamesKapottInput = new FromXLSX().read(kapottFile, kapottMap);
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);

		out.add(new ArrayList<String>(Arrays.asList("Termék kód", "Nettó eladási egységár", "Beszerzési ár (Nettó)",
				"Termék típus", "Raktárkészlet")));
		tools.hangzavarInit();
		for (String key : sheetFromKapott.keySet()) {
			String termekKod = sheetFromKapott.get(key).get(0).replace(".0", "");
			if (termekKod.matches("\\d{2,}.+") && tools.htKeys.contains(termekKod)) {
				String nettoEladasiEgysegar = tools.rounding(sheetFromKapott.get(key).get(4));
				String beszerzesiAr = String.valueOf((Double.parseDouble(nettoEladasiEgysegar) * 0.75));
				String raktarkeszlet = sheetFromKapott.get(key).get(5);
				out.add(new ArrayList<String>(Arrays.asList(termekKod, // Termék_kód
						nettoEladasiEgysegar, // Nettó eladási egységár
						beszerzesiAr, // Beszerzési ár (Nettó)
						"Termék", // Termék típus
						raktarkeszlet // Raktárkészlet
				)));
			}
		}

		voiceKraftToNetsoftArfrissites();
		voiceKraftToNetsoftArlista();
		voiceKraftToShoprenterKeszlet();
	}

	public void voiceKraftToNetsoftArfrissites() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toCSVFile.add(row.get(0) + ";" + row.get(1).replace(".", ","));
		}
		tools.writeToFileCSV("voicek_netsoft_arfriss_", toCSVFile);
	}

	public void voiceKraftToNetsoftArlista() {
//		ArrayList<String> toCSVFile = new ArrayList<>();
//		for (ArrayList<String> row : out) {
//			toCSVFile.add(row.get(0) // Termék kód
//					+ ";" + row.get(1).replace(".", ",") // Nettó eladási egységár
//					+ ";" + row.get(2).replace(".", ",") // Beszerzési ár (Nettó)
//					+ ";" + row.get(3)); // Termék típus
//		}
//		tools.writeToFileCSV("voicek_netsoft_arlistak_", toCSVFile);
		
		LinkedHashMap<String, ArrayList<String>> toNetsoftArlista = new LinkedHashMap<>();
		for (ArrayList<String> row : out) {
			toNetsoftArlista.put(row.get(0), row);
		}
		ArrayList<String> toFile = new Arlistak().calculate(toNetsoftArlista);
		toFile.add(0, "Termék kód" + ";" + "Árlista" + ";" + "Egységár" + ";" + "Alapár (Nettó)" + ";" + "Árrés %" + ";"
				+ "Kedvezmény %");
		for (int i = 0; i < toFile.size(); i++) {
			toFile.set(i, toFile.get(i).replaceFirst("(;[^;]+){2}$", ""));
		}
		tools.writeToFileCSV("voicek_netsoft_arlistak_", toFile);	
	}

	public void voiceKraftToShoprenterKeszlet() throws FileNotFoundException, IOException {
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
		String time = tools.now();
		toxlsx.writeout("voicek_shopr_keszl_" + time + ".xlsx");
	}
	
}