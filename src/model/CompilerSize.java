package model;

import java.io.File;

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

            //Esperar a que se compile
            File f = new File(destiny);
            long length = f.length();

            System.out.println("Pesa " + length + " Bytes");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}