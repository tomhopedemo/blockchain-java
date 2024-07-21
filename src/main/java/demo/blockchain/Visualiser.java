package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Visualiser {

    public void visualise(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);
        System.out.println(json);
    }
}
