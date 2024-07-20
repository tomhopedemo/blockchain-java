package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

public class Visualiser {

    public void visualise(Object object){
        Gson gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .setPrettyPrinting().create();
        String json = gson.toJson(object);
        System.out.println(json);
    }
}
