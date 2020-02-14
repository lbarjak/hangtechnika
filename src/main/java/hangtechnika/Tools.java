package hangtechnika;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Tools implements GlobalVariables {

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
			fw = new FileWriter("../hangtechnika_files/" + nameOfFile + time + ".csv");
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
		String xlsxName = fileName("^hangzavar-xlsx-export-.+\\.xlsx$");
		new FromXLSX().read(xlsxName, HANGZAVAR_MAP);
		htKeys = HANGZAVAR_MAP.get("export").keySet();
	}

	public String round(String numberSring) {
		Integer num = (int) Math.round(Double.parseDouble(numberSring));
		return num.toString();
	}

	public String round(Double number) {
		Integer num = (int) Math.round(number);
		return num.toString();
	}

	public String round2(String numberSring) {
		Double num = Math.round(100.0 * (Double.parseDouble(numberSring))) / 100.0;
		DecimalFormat df = new DecimalFormat("0,00");
		return df.format(num);
	}

	public String decimalFormat(double number) {
		Locale locale = new Locale("hu", "HU");
		String pattern = "0.00";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		String formatted = decimalFormat.format(number);
		return formatted;
	}

	String fileName(String regex) {
		File folder = new File("../hangtechnika_files");
		ArrayList<File> listOfXLSX = new ArrayList<>();
		if (folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().matches(regex)) {
					listOfXLSX.add((File) file);
				}
			}
			File[] listOfXLSXToSort = new File[listOfXLSX.size()];
			listOfXLSXToSort = listOfXLSX.toArray(listOfXLSXToSort);
			sortFilesByDateCreated(listOfXLSXToSort);
			return folder + "/" + listOfXLSXToSort[0].getName();
		} else {
			System.out.println("A folder nem létezik!");
		}
		return "";
	}

	public void sortFilesByDateCreated(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long l1 = getFileCreationEpoch(f1);
				long l2 = getFileCreationEpoch(f2);
				//return Long.valueOf(l1).compareTo(l2);
				return Long.valueOf(l2).compareTo(l1);//reverse
			}
		});
	}

	public long getFileCreationEpoch(File file) {
		try {
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			return attr.creationTime().toInstant().toEpochMilli();
		} catch (IOException e) {
			throw new RuntimeException(file.getAbsolutePath(), e);
		}
	}

}
