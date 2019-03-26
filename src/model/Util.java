package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {

    public static String deleteExtension(String file) {
        return file.substring(0, file.lastIndexOf("."));
    }

    public static boolean isFileCompatible(String file) {
        return file.substring(file.lastIndexOf(".")).equals(".c") ||
                file.substring(file.lastIndexOf(".")).equals(".cpp");
    }

    public static List<String> getCFlags(){
        String command = "gcc --help=optimizers";
        return getFlagsWithCommand(command);
    }

    public static List<String> getCPPFlags(){
        String command = "g++ --help=optimizers";
        return getFlagsWithCommand(command);
    }

    private static List<String> getFlagsWithCommand(String command){
        List<String> flags = new ArrayList<>();

        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(command).getInputStream());
            while(s.hasNext()){
                String token = s.next();
                if(!token.contains("=") && token.length()>1 && token.substring(0,2).equals("-f")) {flags.add(token);}
            }
        } catch (IOException e) {}

        return flags;
    }
}
