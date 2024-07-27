package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Visualiser {

    public void visualise(Object... objects){
        for (Object object : objects) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(object);
            System.out.println(json);
        }
    }
}
