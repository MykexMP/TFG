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
                token = "";
                p = Runtime.getRuntime().exec(compileCommand + " " + flag); //FIXME LANZA EXCEPCION
                if(p.waitFor()==0) {
                    s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream());
                    while(s.hasNext()&&!token.equals("TotalMilliseconds"))
                    {
                        token = s.next();
                        if(token.equals("TotalMilliseconds"))
                        {
                            s.next();

                            float execTime = Float.parseFloat(s.next().replace(",","."));
                            System.out.println("El flag '" + flag + "' hace que tarde " + execTime + " Milisegundos");

                            if(  execTime < baseExecTime*(1-threshold))     flagsProfit.add(flag);
                        }
                    }
                }
                else flagsNoExec.add(flag);
            }

            // Son los flags que el código del proceso devuelto no es 0.
            System.out.println("Los siguientes flags no han podido aplicarse:");
            for (String f: flagsNoExec ) {
                System.out.println("El flag ' " + f + "'");
            }

            //
            System.out.println("Los siguientes flags mejoran el tiempo:");
            for (String f: flagsProfit ) {
                System.out.println("El flag ' " + f + "'");
            }

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}
