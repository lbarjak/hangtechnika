package hangtechnika;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Arlistak {
	
	private Tools tools = new Tools();

	private ArrayList<String> toFile = new ArrayList<>();
	private boolean monacor = false;

	public ArrayList<String> calculate(LinkedHashMap<String, ArrayList<String>> toNetsoftArlista) {

		int nettoEladasiEgysegarIndex = toNetsoftArlista.get("Termék kód").indexOf("Nettó eladási egységár");
		int nettoBeszerzesiEgysegarIndex = toNetsoftArlista.get("Termék kód").indexOf("Beszerzési ár (Nettó)");
		int termekTipusIndex = toNetsoftArlista.get("Termék kód").indexOf("Termék típus");
		double nettoBeszerzesiEgysegar = 0;
		double nettoEladasiEgysegar = 0;
		String termekTipus = "";
		double torzsvasarlo_2_arany = 0.6;
		double torzsvasarlo_5_nagyker_arany = 0.225;
		double minimumArszorzo = 1.1099;
//		double torzsvasarlo_2_arany = 1;
//		double torzsvasarlo_5_nagyker_arany = 1;
//		double minimumArszorzo = 1;
		double torzsvasarlo_2 = 0;
		double torzsvasarlo_5_nagyker = 0;
		String kedvezmenyTorzsvasarlo_2_Percent = "";
		String kedvezmenyTorzsvasarlo_5_nagykerPercent = "";
		String arresPercent;

		for (String key : toNetsoftArlista.keySet()) {

			if (!toNetsoftArlista.get(key).get(nettoBeszerzesiEgysegarIndex).equals("Beszerzési ár (Nettó)")) {

				nettoBeszerzesiEgysegar = Double.parseDouble(toNetsoftArlista.get(key).get(nettoBeszerzesiEgysegarIndex)
						.replace("\"", "").replace(",", "."));
				nettoEladasiEgysegar = Double.parseDouble(
						toNetsoftArlista.get(key).get(nettoEladasiEgysegarIndex).replace("\"", "").replace(",", "."));
				termekTipus = toNetsoftArlista.get(key).get(termekTipusIndex);

				if (nettoBeszerzesiEgysegar == 0 && nettoEladasiEgysegar == 0) {
					toFile.add(key + ";" + "Törzsvásárló 2" + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0);
					toFile.add(key + ";" + "Törzsvásárló 5-nagyker" + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0);
					continue;
				}

				arresPercent = tools.decimalFormat((1 - nettoBeszerzesiEgysegar / nettoEladasiEgysegar) * 100);

				if (termekTipus.equals("Termék")) {

					torzsvasarlo_2 = (nettoEladasiEgysegar / minimumArszorzo - nettoBeszerzesiEgysegar)
							* torzsvasarlo_2_arany + nettoBeszerzesiEgysegar * minimumArszorzo;

					torzsvasarlo_5_nagyker = (nettoEladasiEgysegar / minimumArszorzo - nettoBeszerzesiEgysegar)
							* torzsvasarlo_5_nagyker_arany + nettoBeszerzesiEgysegar * minimumArszorzo;
					if (monacor == true) {
						torzsvasarlo_5_nagyker = nettoBeszerzesiEgysegar * 1.12;
					}

					kedvezmenyTorzsvasarlo_2_Percent = tools.decimalFormat((1 - torzsvasarlo_2 / nettoEladasiEgysegar) * 100);
					kedvezmenyTorzsvasarlo_5_nagykerPercent = tools.decimalFormat(
							(1 - torzsvasarlo_5_nagyker / nettoEladasiEgysegar) * 100);

					toFile.add(key + ";" + "Törzsvásárló 2" + ";" + tools.decimalFormat(torzsvasarlo_2) + ";"
							+ tools.decimalFormat(nettoEladasiEgysegar) + ";" + arresPercent + ";"
							+ kedvezmenyTorzsvasarlo_2_Percent);
					toFile.add(key + ";" + "Törzsvásárló 5-nagyker" + ";" + tools.decimalFormat(torzsvasarlo_5_nagyker) + ";"
							+ tools.decimalFormat(nettoEladasiEgysegar) + ";" + arresPercent + ";"
							+ kedvezmenyTorzsvasarlo_5_nagykerPercent);
				} else if (termekTipus.equals("Szolgáltatás")) {
					toFile.add(key + ";" + "Törzsvásárló 2" + ";" + tools.decimalFormat(nettoEladasiEgysegar) + ";"
							+ tools.decimalFormat(nettoEladasiEgysegar) + ";" + arresPercent + ";" + 0);
					toFile.add(key + ";" + "Törzsvásárló 5-nagyker" + ";" + tools.decimalFormat(nettoBeszerzesiEgysegar) + ";"
							+ tools.decimalFormat(nettoEladasiEgysegar) + ";" + arresPercent + ";" + 0);
				}
			}
		}
		return toFile;
	}

//	public static String decimalFormat(double number) {
//
//		Locale locale = new Locale("hu", "HU");
//		String pattern = ".##";
//		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
//		decimalFormat.applyPattern(pattern);
//		String formatted = decimalFormat.format(number);
//		return formatted;
//	}

}
