package model.compiler;

import model.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompilerEficiency extends Compiler{
    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerEficiency.class.getName())){
            instance = new CompilerEficiency();
            return instance;
        }else return instance;
    }

    @Override
    public void compile(String path, int threshold, List<String> flags) {
        String destiny = Util.deleteExtension(path) + ".exe";

        String compileCommand = "g++ -o " + destiny + " " + path;
        String execCommand = "powershell.exe Measure-Command{" + destiny + "}";

        Process p ;

        float baseExecTime=0;

        try {
            p = Runtime.getRuntime().exec(compileCommand);
            p.waitFor();
            Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream());
            String token="";

            while(s.hasNext()&&!token.equals("TotalMilliseconds"))
            {
                token = s.next();
                if(token.equals("TotalMilliseconds"))
                {
                    s.next();
                    // Time of the base compilation
                    baseExecTime = Float.parseFloat(s.next().replace(",","."));
                }
            }

            s.close();

            System.out.println("Ha tardado " + baseExecTime + " milisegundos");

            List<String> flagsProfit = new ArrayList<>();
            List<String> flagsNoExec = new ArrayList<>();

            for (String flag : flags) {
                p = Runtime.getRuntime().exec(compileCommand + " " + flag); //FIXME LANZA EXCEPCION
                if(p.waitFor()==0) {
                    s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream());
                    while(s.hasNext()&&!token.equals("TotalMilliseconds"))
                    {
                        token = s.next();
                        if(token.equals("TotalMilliseconds"))
                        {
                            s.next();
                            // Time of the base compilation

                            float execTime = Float.parseFloat(s.next().replace(",","."));

                            System.out.println("El flag '" + s + "' hace que tarde " + execTime + " Bytes");

                            if(  execTime < baseExecTime*(1-threshold))     flagsProfit.add(flag);
                        }
                    }
                }
                else flagsNoExec.add(flag);
            }

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}
