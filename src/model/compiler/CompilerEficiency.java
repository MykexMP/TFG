package model.compiler;

import model.Util;

import java.util.Scanner;

public class CompilerEficiency extends Compiler{
    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerEficiency.class.getName())){
            instance = new CompilerEficiency();
            return instance;
        }else return instance;
    }

    @Override
    public void compile(String path,int threshold) {
        String destiny = Util.deleteExtension(path);

        String compileCommand = "g++ -o " + destiny + " " + path;
        String execCommand = "powershell.exe Measure-Command{" + destiny + "}";

        float execTime=0;

        try {
            Runtime.getRuntime().exec(compileCommand);
            Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream());
            String token="";

            while(s.hasNext()&&!token.equals("TotalMilliseconds"))
            {
                token = s.next();
                if(token.equals("TotalMilliseconds"))
                {
                    s.next();
                    // Time of the base compilation
                    execTime = Float.parseFloat(s.next().replace(",","."));
                }
            }

            System.out.println("Ha tardado " + execTime + " milisegundos");

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}
