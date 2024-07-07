package demo.service;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBN {

    public static void main(String[] args) throws IOException {
        String baseDir = "/Users/tom/datas/";
        String outputsDir = baseDir + "outputs/javas/";
        Map<String, Object> statisticsMap = new HashMap<>();

        List<String> speciesData = Files.readLines(new File(baseDir + "Atlas_of_European_Mammals_-_Rodentia.csv"), Charset.defaultCharset());
        String csvHeader = speciesData.getFirst();

        speciesData.removeFirst();
        String reversed = String.join("\n", speciesData.reversed());

        statisticsMap.put("size", speciesData.size());
        Files.write((csvHeader + "\n" + reversed).getBytes(), new File(outputsDir + "ReverseRodents.csv"));
        Files.write(new Gson().toJson(statisticsMap).getBytes(), new File(outputsDir + "RodentsStats.csv"));
    }
}
