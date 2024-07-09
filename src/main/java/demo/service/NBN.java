package demo.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.readAllLines;

public class NBN {

    public static void main(String[] args) throws Exception {
        String baseDir = "/Users/tom/datas/";
        String outputsDir = baseDir + "outputs/javas/";
        String inputFileName = baseDir + "Atlas_of_European_Mammals_-_Rodentia.csv";

        Map<String, Object> statisticsMap = new HashMap<>();

        //Read As CSV
        CSV csv = CSV.readParse(inputFileName);

        //Read As Lines
        List<String> speciesData = readAllLines(Path.of(inputFileName));
        String csvHeader = speciesData.getFirst();

        speciesData.removeFirst();
        String reversed = String.join("\n", speciesData.reversed());

        //Write as Json
        List<Map<String, String>> jsonRecords = CSV.csvToJson(csv);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        statisticsMap.put("size", speciesData.size());
        java.nio.file.Files.write(Path.of(outputsDir + "ReverseRodents.csv"), (csvHeader + "\n" + reversed).getBytes());
        java.nio.file.Files.write(Path.of(outputsDir + "RodentsStats.csv"), gson.toJson(statisticsMap).getBytes());
        java.nio.file.Files.write(Path.of(outputsDir + "RodentsJson.json"), gson.toJson(jsonRecords).getBytes());
    }

}
