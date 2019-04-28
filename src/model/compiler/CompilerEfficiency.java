package model.compiler;

import java.util.List;
import java.util.Scanner;

public class CompilerEfficiency extends Compiler{
    private float minTime = Float.MAX_VALUE;
    private float baseTime;
    private float time;

    private Scanner s;

    private String executeCommand = System.getProperty("os.name").startsWith("Windows") ? "powershell.exe Measure-Command{" + destiny + "}" : "time " + destiny;

    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerEfficiency.class.getName())){
            instance = new CompilerEfficiency();
            return instance;
        }else return instance;
    }

    @Override
    public void compile(String origin, int threshold, List<String> flags) {
        init(origin);
        getProfitFlags(flags,threshold);
        getFinalCommand();
        restartVariables();
    }

    @Override
    protected void getProfitFlags(List<String> flags, int threshold) {
        try{
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()!=0) throw new Exception();
            s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());

            while(s.hasNext())
            {
                if(s.next().equals("TotalMilliseconds"))
                {
                    s.next();
                    baseTime = Float.parseFloat(s.next().replace(",","."));
                    break;
                }
            }

            s.close();

            for (String flag : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + flag).waitFor()==0) {
                    s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
                    while(s.hasNext())
                    {
                        if(s.next().equals("TotalMilliseconds"))
                        {
                            s.next();
                            float time = Float.parseFloat(s.next().replace(",","."));
                            System.out.println("El flag '" + flag + "' hace que tarde " + time + " Milisegundos");
                            if(  time < baseTime*(1-threshold))     flagsProfit.add(flag);
                            break;
                        }
                    }
                    s.close();
                }
                else flagsNoExecuted.add(flag);
            }
        }
        catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void getFinalCommand() {
        try {
            for(int i=0;i<1000;i++) {
                generateCommand();

                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);

                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand);

                    if(Runtime.getRuntime().exec(combinedFlagsCommand).waitFor()==0){
                        s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
                        while(s.hasNext())
                        {
                            if(s.next().equals("TotalMilliseconds"))
                            {
                                s.next();
                                time = Float.parseFloat(s.next().replace(",","."));
                                System.out.println(" y tarda: " + time);
                                if(time<minTime) {minTime = time; finalCommand=combinedFlagsCommand;}
                            }
                        }
                        s.close();
                    }
                }
            }

            System.out.println("EL MEJOR COMANDO ES '" + finalCommand + "' Y TARDA " + minTime + " MILISEGUNDOS. ");

            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
            restartVariables();

        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void restartVariables() {
        minTime = Float.MAX_VALUE;
        baseTime = 0;
        time = 0;
        super.restartVariables();
    }
}
