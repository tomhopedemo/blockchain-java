package demo.toJson;

import demo.objects.CSV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVtoJSON {

    public CSVtoJSON() {
    }

    public List<Map<String, String>> csvToJson(CSV csv) {
        List<Map<String, String>> jsonRecords = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < csv.csvRows().size(); rowIndex++) {
            LinkedHashMap<String, String> orderedJsonMap = new LinkedHashMap<>();
            List<String> row = csv.csvRows().get(rowIndex);
            for (int i = 0; i < csv.csvHeaderFields().size(); i++) {
                orderedJsonMap.put(csv.csvHeaderFields().get(i), row.get(i));
            }
            jsonRecords.add(orderedJsonMap);
        }
        return jsonRecords;
    }
}
