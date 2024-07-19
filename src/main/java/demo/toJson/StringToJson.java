package demo.toJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StringToJson {
    public static String stringToJson(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }
}
