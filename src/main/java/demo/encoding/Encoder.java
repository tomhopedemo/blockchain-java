package demo.encoding;

import java.security.Key;
import java.util.Base64;

public class Encoder {

    public static String encode(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
