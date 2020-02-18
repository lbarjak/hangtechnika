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
		double torzsvasarlo_arany = 0.6;
		double nagyker_arany = 0.225;
		double minimumArszorzo = 1.1099;
//		double torzsvasarlo_arany = 1;
//		double nagyker_arany = 1;
//		double minimumArszorzo = 1;
		double torzsvasarlo = 0;
		double nagyker = 0;
		String kedvezmenyTorzsvasarloPercent = "";
		String kedvezmenyNagykerPercent = "";
		String arresPercent;

		for (String key : toNetsoftArlista.keySet()) {

			if (!toNetsoftArlista.get(key).get(nettoBeszerzesiEgysegarIndex).equals("Beszerzési ár (Nettó)")) {

				nettoBeszerzesiEgysegar = Double.parseDouble(toNetsoftArlista.get(key).get(nettoBeszerzesiEgysegarIndex)
						.replace("\"", "").replace(",", "."));
				nettoEladasiEgysegar = Double.parseDouble(
						toNetsoftArlista.get(key).get(nettoEladasiEgysegarIndex).replace("\"", "").replace(",", "."));
				termekTipus = toNetsoftArlista.get(key).get(termekTipusIndex);

				if (nettoBeszerzesiEgysegar == 0 && nettoEladasiEgysegar == 0) {
					toFile.add(key + ";" + "Törzsvásárló" + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0);
					toFile.add(key + ";" + "Nagyker" + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0);
					continue;
				}

				arresPercent = tools.decimalFormat((1 - nettoBeszerzesiEgysegar / nettoEladasiEgysegar) * 100);

				if (termekTipus.equals("Termék")) {

					torzsvasarlo = (nettoEladasiEgysegar / minimumArszorzo - nettoBeszerzesiEgysegar)
							* torzsvasarlo_arany + nettoBeszerzesiEgysegar * minimumArszorzo;

					nagyker = (nettoEladasiEgysegar / minimumArszorzo - nettoBeszerzesiEgysegar)
							* nagyker_arany + nettoBeszerzesiEgysegar * minimumArszorzo;
					if (monacor == true) {
						nagyker = nettoBeszerzesiEgysegar * 1.12;
					}

					kedvezmenyTorzsvasarloPercent = tools.decimalFormat((1 - torzsvasarlo / nettoEladasiEgysegar) * 100);
					kedvezmenyNagykerPercent = tools.decimalFormat(
							(1 - nagyker / nettoEladasiEgysegar) * 100);

					toFile.add(key + ";" + "Törzsvásárló" + ";" + tools.round(torzsvasarlo) + ";"
							+ tools.round(nettoEladasiEgysegar) + ";" + arresPercent + ";"
							+ kedvezmenyTorzsvasarloPercent);
					toFile.add(key + ";" + "Nagyker" + ";" + tools.round(nagyker) + ";"
							+ tools.round(nettoEladasiEgysegar) + ";" + arresPercent + ";"
							+ kedvezmenyNagykerPercent);
				} else if (termekTipus.equals("Szolgáltatás")) {
					toFile.add(key + ";" + "Törzsvásárló" + ";" + tools.round(nettoEladasiEgysegar) + ";"
							+ tools.round(nettoEladasiEgysegar) + ";" + arresPercent + ";" + 0);
					toFile.add(key + ";" + "Nagyker" + ";" + tools.round(nettoBeszerzesiEgysegar) + ";"
							+ tools.round(nettoEladasiEgysegar) + ";" + arresPercent + ";" + 0);
				}
			}
		}
		return toFile;
	}

}
