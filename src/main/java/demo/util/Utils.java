package demo.util;

public class Utils {
    
    public static boolean startsWith(String string, String prefix){
        if (string == null || prefix == null){
            return false;
        }
        return string.startsWith(prefix);
    }
}
