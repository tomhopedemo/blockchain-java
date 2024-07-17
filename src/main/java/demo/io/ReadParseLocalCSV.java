package demo.io;

import com.opencsv.CSVReader;
import demo.objects.CSV;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadParseLocalCSV {

    String localFileAbsolutePath;

    public ReadParseLocalCSV(String localFileAbsolutePath) {
        this.localFileAbsolutePath = localFileAbsolutePath;
    }

    public CSV readParse() throws Exception {
        try (CSVReader csvReader = new CSVReader(new FileReader(localFileAbsolutePath))) {
            List<List<String>> csvRows = new ArrayList<>();
            List<String> csvHeaderFields = Arrays.asList(csvReader.readNext());
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                csvRows.add(Arrays.asList(values));
            }
            return new CSV(csvHeaderFields, csvRows);
        }
    }
}
