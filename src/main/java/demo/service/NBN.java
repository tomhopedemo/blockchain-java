package demo.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.readAllLines;

public class NBN {

    public static void main(String[] args) throws Exception {
        String baseDir = "/Users/tom/datas/";
        String outputsDir = baseDir + "outputs/javas/";
        Map<String, Object> statisticsMap = new HashMap<>();

        //Read As CSV
        List<List<String>> csvRows = new ArrayList<>();
        List<String> csvHeaderFields;
        try (CSVReader csvReader = new CSVReader(new FileReader(baseDir + "Atlas_of_European_Mammals_-_Rodentia.csv"))) {
            csvHeaderFields = Arrays.asList(csvReader.readNext());
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                csvRows.add(Arrays.asList(values));
            }
        }

        //Read As Lines
        List<String> speciesData = readAllLines(Path.of(baseDir + "Atlas_of_European_Mammals_-_Rodentia.csv"));
        String csvHeader = speciesData.getFirst();

        speciesData.removeFirst();
        String reversed = String.join("\n", speciesData.reversed());

        //Write as Json
        List<Map<String, String>> jsonRecords = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < csvRows.size(); rowIndex++) {
            LinkedHashMap<String, String> orderedJsonMap = new LinkedHashMap<>();
            List<String> row = csvRows.get(rowIndex);
            for (int i = 0; i < csvHeaderFields.size(); i++) {
                orderedJsonMap.put(csvHeaderFields.get(i), row.get(i));
            }
            jsonRecords.add(orderedJsonMap);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        statisticsMap.put("size", speciesData.size());
        java.nio.file.Files.write(Path.of(outputsDir + "ReverseRodents.csv"), (csvHeader + "\n" + reversed).getBytes());
        java.nio.file.Files.write(Path.of(outputsDir + "RodentsStats.csv"), gson.toJson(statisticsMap).getBytes());
        java.nio.file.Files.write(Path.of(outputsDir + "RodentsJson.json"), gson.toJson(jsonRecords).getBytes());
    }
}
