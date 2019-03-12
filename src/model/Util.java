package model;

public class Util {

    public static String deleteExtension(String file){
        return file.substring(0,file.lastIndexOf("."));
    }
}
