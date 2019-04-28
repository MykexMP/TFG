package model.compiler;

import model.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        String execCommand = System.getProperty("os.name").startsWith("Windows") ? "powershell.exe Measure-Command{" + destiny + "}" : "time " + destiny;

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
                p = Runtime.getRuntime().exec(compileCommand + " " + flag);
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

            // FASE 2
            Random r = new Random();
            String compileBestCommand = compileCommand;
            float minExecTime = Float.MAX_VALUE;
            String finalCommand="";
            List<String> used = new ArrayList<>();

            for(int i=0;i<1000;i++) {
                compileBestCommand = compileCommand;
                for (String f : flagsProfit) {
                    if (r.nextInt() % 2 == 0) {
                        compileBestCommand = compileBestCommand + " " + f;
                        //System.out.println("El flag ' " + f + "' esta en la muestra");
                    }
                }

                if(!used.contains(compileBestCommand)){

                used.add(compileBestCommand);

                System.out.println("El comando a ejecutar es: " + (i+1));
                System.out.println(compileBestCommand);

                Process pr = Runtime.getRuntime().exec(compileBestCommand);
                if(pr.waitFor()==0){
                    token = "";
                    s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream());
                    while(s.hasNext()&&!token.equals("TotalMilliseconds"))
                    {
                        token = s.next();
                        if(token.equals("TotalMilliseconds"))
                        {
                            s.next();
                            float execTime = Float.parseFloat(s.next().replace(",","."));
                            System.out.println(" y tarda: " + execTime);
                            if(execTime<minExecTime) {minExecTime = execTime; finalCommand=compileBestCommand;}
                        }
                    }
                }
            }
        }

            System.out.println("EL MEJOR COMANDO ES '" + finalCommand + "' Y TARDA " + minExecTime + " MILISEGUNDOS. ");

            Process finalExecProc = Runtime.getRuntime().exec(finalCommand);
            if(finalExecProc.waitFor()==0){
                System.out.println("Ejecutable generado con éxito");
            }

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void getProfitFlags(List<String> flags, int threshold) {

    }

    @Override
    protected void getFinalCommand() {

    }
}
