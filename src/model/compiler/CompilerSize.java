package model.compiler;

import model.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompilerSize extends Compiler {
    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerSize.class.getName())){
            instance = new CompilerSize();
            return instance;
        }else return instance;
    }

    @Override
    public void compile(String path, int threshold, List<String> flags) {
        String destiny = Util.deleteExtension(path) + ".exe";
        String compileCommand = "g++ -o " + destiny + " " + path;

        try {
            Process p = Runtime.getRuntime().exec(compileCommand);
            p.waitFor();

            File f = new File(destiny);
            long baseLength = f.length();

            System.out.println("Pesa " + baseLength + " Bytes");
            List<String> flagsProfit = new ArrayList<>();
            List<String> flagsNoExec = new ArrayList<>();

            for (String s : flags) {
                p = Runtime.getRuntime().exec(compileCommand + " " + s);
                if(p.waitFor()==0) {
                    long length = new File(destiny).length();
                    System.out.println("El flag '" + s + "' le da tamaño " + length + " Bytes" );
                    if (baseLength*(1-threshold) > length ) flagsProfit.add(s);
                }
                else flagsNoExec.add(s);
            }
            // Son los flags que el código del proceso devuelto no es 0.
            System.out.println("Los siguientes flags no han podido aplicarse:");

            for (String s: flagsNoExec ) {
                System.out.println("El flag ' " + s + "'");
            }

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}