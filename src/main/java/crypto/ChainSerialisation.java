package crypto;

import com.google.gson.GsonBuilder;

public class ChainSerialisation {

    public static Blockchain deserialise(String serialisedChain){
        return new GsonBuilder().create().fromJson(serialisedChain, Blockchain.class);
    }

    public static String serialise(Blockchain chain){
        return new GsonBuilder().create().toJson(chain);
    }

}
