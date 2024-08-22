package crypto.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static crypto.blockchain.Control.VISUALIZE_IN_CONSOLE;

public class Visualiser {

    public static void visualise(Object... objects){
        for (Object object : objects) {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(object);
            System.out.println(json);

            gson = new GsonBuilder().setPrettyPrinting().create();
            json = gson.toJson(object);
            System.out.println(json);
        }
    }
}
