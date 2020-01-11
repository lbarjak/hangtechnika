package hangtechnika;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Voicekraft {

	private Tools tools = new Tools();
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private ArrayList<ArrayList<String>> sheetNamesKapottInput;
	private String sheetNameFromKapott;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private final String kapottFile = "bb11a3a45af20fe453cca9a783effd05_VK_Arlista.xlsx";
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();
	private final ArrayList<ArrayList<String>> out = new ArrayList<>();

	public void convert() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {

		sheetNamesKapottInput = new FromXLSX().read(kapottFile, kapottMap); // amit kapunk
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);

		out.add(new ArrayList<String>(Arrays.asList("Termék kód", "Nettó eladási egységár", "Beszerzési ár (Nettó)",
				"Termék típus", "Raktárkészlet")));
		for (String key : sheetFromKapott.keySet()) {
			if (sheetFromKapott.get(key).get(0).matches("\\d{2,}.+")) {
				out.add(new ArrayList<String>(Arrays.asList(sheetFromKapott.get(key).get(0).replace(".0", ""), // Termék_kód
						sheetFromKapott.get(key).get(4), // Nettó eladási egységár
						df2.format(Double.parseDouble(sheetFromKapott.get(key).get(4)) * 0.75), // Beszerzési ár (Nettó)
						"Termék", // Termék típus
						sheetFromKapott.get(key).get(5) // Raktárkészlet
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
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toCSVFile.add(row.get(0) // Termék kód
					+ ";" + row.get(1).replace(".", ",") // Nettó eladási egységár
					+ ";" + row.get(2) + ";" // Beszerzési ár (Nettó)
					+ row.get(3)); // Termék típus
		}
		tools.writeToFileCSV("voicek_netsoft_arlistak_", toCSVFile);
	}

	public void voiceKraftToShoprenterKeszlet() throws FileNotFoundException, IOException {
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenterMap = tools.prebuildToShoprenter();
		ToXLSX toxlsx = new ToXLSX();
		LinkedHashMap<String, ArrayList<String>> export = toShoprenterMap.get("export");
		for (ArrayList<String> row : out) {
			ArrayList<String> reducedRow = new ArrayList<>();
			reducedRow.add(row.get(0));
			reducedRow.add(row.get(4));
			export.put(row.get(0), reducedRow);
		}
		export.remove("Termék kód");
		toxlsx.write(toShoprenterMap);
		String time = tools.now();
		toxlsx.writeout("voicek_shopr_keszl" + time + ".xlsx");
	}
}