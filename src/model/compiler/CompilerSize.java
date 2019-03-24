package model.compiler;

import model.Util;

import java.io.File;
import java.util.List;

public class CompilerSize extends Compiler {
    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerSize.class.getName())){
            instance = new CompilerSize();
            return instance;
        }else return instance;
    }

    @Override
    public void compile(String path,int threshold) {
        String destiny = Util.deleteExtension(path) + ".exe";
        String compileCommand = "g++ -o " + destiny + " " + path;

        try {
            Runtime.getRuntime().exec(compileCommand);
            while(new File(destiny).length()==0) {} //Espera Activa

            File f = new File(destiny);
            long baseLength = f.length();

            System.out.println("Pesa " + baseLength + " Bytes");

            /* FIXME

            List<String> flags = Util.getFlags();
            List<String> flagsProfit;

            for (String s : flags) {
                Runtime.getRuntime().exec(compileCommand + " " + s);
                long length = new File(destiny).length(); // ¿ Como esperar ?
                if (baseLength*(1-threshold) > length ){
                    System.out.println("El flag '" + s + "' aumenta la eficiencia." );
                }
            }

            */
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}