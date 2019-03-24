package model;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String deleteExtension(String file) {
        return file.substring(0, file.lastIndexOf("."));
    }

    public static boolean isFileCompatible(String file) {
        return file.substring(file.lastIndexOf(".")).equals(".c") ||
                file.substring(file.lastIndexOf(".")).equals(".cpp");
    }

    public static List<String> getFlags(){
        // FIXME
        List<String> flags = new ArrayList<>();
        return flags;
    }
}
