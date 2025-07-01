import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** @author chatGPT 23.06.2025 */
public class InfoAccessToCSV {

	public static void main(String[] args) throws IOException {
		String folderPath = "C:\\0-PartAurore\\20160131-CSS5\\20171006-CI\\20171106-SQL";
		File[] files = new File(folderPath).listFiles((dir, name) -> name.endsWith(".sql"));
		if (files == null) return;

		Map<String, Map<Integer, Integer>> dataByDate = new TreeMap<>();
		Set<Integer> allIds = new TreeSet<>();

		for (File file : files) {
			String content = Files.readString(file.toPath());
			String dateRaw = file.getName().split("-")[0]; // ex: 20200203
			String formattedDate = formatDate(dateRaw);

			Map<Integer, Integer> accessMap = new HashMap<>();

			Pattern insertPattern = Pattern.compile("INSERT INTO `info_access`.*?VALUES\\s*(.*?);", Pattern.DOTALL);
			Matcher matcher = insertPattern.matcher(content);

			if (matcher.find()) {
				String valuesBlock = matcher.group(1);
				Pattern tuplePattern = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)");
				Matcher valueMatcher = tuplePattern.matcher(valuesBlock);
				while (valueMatcher.find()) {
					int idInformation = Integer.parseInt(valueMatcher.group(2));
					int nbAccess = Integer.parseInt(valueMatcher.group(3));
					accessMap.put(idInformation, nbAccess);
					allIds.add(idInformation);
				}
				dataByDate.put(formattedDate, accessMap);
			}
		}

		String outputPath = "info_access_output.csv";
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath)))) {
			// Écriture des en-têtes
			writer.print("Date");
			for (int id : allIds) {
				writer.print("," + id);
			}
			writer.println();

			// Écriture des lignes
			for (String date : dataByDate.keySet()) {
				writer.print(date);
				Map<Integer, Integer> accessMap = dataByDate.get(date);
				for (int id : allIds) {
					writer.print(",");
					Integer value = accessMap.get(id);
					writer.print(value != null ? value : "");
				}
				writer.println();
			}
		}

		System.out.println("✅ CSV généré avec succès : " + outputPath);
	}

	// Convertit une date 20200203 → 2020-02-03
	private static String formatDate(String raw) {
		return raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8);
	}
}
