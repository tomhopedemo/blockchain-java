package demo.service;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.*;

public class CSV {

    List<String> csvHeaderFields;
    List<List<String>> csvRows = new ArrayList<>();

    public CSV(List<String> csvHeaderFields, List<List<String>> csvRows) {
        this.csvHeaderFields = csvHeaderFields;
        this.csvRows = csvRows;
    }

    public List<String> getCsvHeaderFields() {
        return csvHeaderFields;
    }

    public List<List<String>> getCsvRows() {
        return csvRows;
    }

    public static CSV readParse(String inputFileName) throws Exception {
        try (CSVReader csvReader = new CSVReader(new FileReader(inputFileName))) {
            List<List<String>> csvRows = new ArrayList<>();
            List<String> csvHeaderFields = Arrays.asList(csvReader.readNext());
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                csvRows.add(Arrays.asList(values));
            }
            return new CSV(csvHeaderFields, csvRows);
        }
    }

    public static List<Map<String, String>> csvToJson(CSV csv) {
        List<List<String>> csvRows = csv.csvRows;
        List<String> csvHeaderFields = csv.csvHeaderFields;
        List<Map<String, String>> jsonRecords = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < csvRows.size(); rowIndex++) {
            LinkedHashMap<String, String> orderedJsonMap = new LinkedHashMap<>();
            List<String> row = csvRows.get(rowIndex);
            for (int i = 0; i < csvHeaderFields.size(); i++) {
                orderedJsonMap.put(csvHeaderFields.get(i), row.get(i));
            }
            jsonRecords.add(orderedJsonMap);
        }
        return jsonRecords;
    }
}
