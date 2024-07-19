package demo.toJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ObjectToJson {

    public static String objectToJson(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

}
