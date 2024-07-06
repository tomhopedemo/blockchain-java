package demo.service;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBN {

    public static void main(String[] args) throws Exception {
        Map<String, String> statisticsMap = new HashMap<>();

        List<String> strings = Files.readLines(new File("/Users/tom/datas/Atlas_of_European_Mammals_-_Rodentia.csv"), Charset.defaultCharset());

        //statistics map (size)
        int size = strings.size();
        statisticsMap.put("size", String.valueOf(size));
        System.out.println(size);
        //reverse
        List<String> reversed = strings.reversed();
        //join
        String joined = String.join("\n", reversed);
        //write
        Files.write(joined.getBytes(), new File("/Users/tom/datas/outputs/javas/ReverseRodents.csv"));

        Gson gson = new Gson();
        String jsonStatisticsMap = gson.toJson(statisticsMap);
        Files.write(jsonStatisticsMap.getBytes(), new File("/Users/tom/datas/outputs/javas/RodentsStats.csv"));
    }
}
