package model;

import model.flag.Flag;

import java.io.IOException;
import java.util.*;

public class Util {

    public static String deleteExtension(String file) {
        return file.substring(0, file.lastIndexOf("."));
    }

    public static boolean isFileCompatible(String file) {
        if(file.contains("."))
        {
            return file.substring(file.lastIndexOf(".")).equals(".c") ||
                    file.substring(file.lastIndexOf(".")).equals(".cpp");
        }
        else
        {
            return false;
        }
    }

    public static String getfnoFlag(String flag) {
        return "-fno-" + flag.substring(2);
    }

    public static List<String> getCFlags(){
        String command = "gcc --help=optimizers";
        return getFlagsWithCommand(command);
    }

    public static List<String> getCPPFlags(){
        String command = "g++ --help=optimizers";
        return getFlagsWithCommand(command);
    }

    public static Set<Flag> randomizeFlagSortedSet(Set<Flag> flagSetSorted){
        List<Flag> flagList = new ArrayList<>(flagSetSorted);
        Collections.shuffle(flagList);
        HashSet<Flag> flagSet = new HashSet<>(flagList);
        return flagSet;
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
