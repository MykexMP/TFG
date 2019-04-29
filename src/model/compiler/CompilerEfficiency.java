package model.compiler;

import java.util.List;
import java.util.Scanner;

public class CompilerEfficiency extends Compiler{
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
            baseValue = System.getProperty("os.name").startsWith("Windows") ? getTimeOnWindows() : getTimeOnUnix();

            for (String flag : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + flag).waitFor()==0) {
                    value = getTimeOnWindows();
                    System.out.println("El flag '" + flag + "' hace que tarde " + value + " Milisegundos");
                    if(value < baseValue *(1-threshold)) flagsProfit.add(flag);
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
                generateCommandByRandomSearch();

                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);

                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand);

                    if(Runtime.getRuntime().exec(combinedFlagsCommand).waitFor()==0){
                        value = getTimeOnWindows();
                        System.out.println(" y tarda: " + value);
                        if(value < minValue) {
                            minValue = value; finalCommand=combinedFlagsCommand;}
                    }
                }
            }

            System.out.println("EL MEJOR COMANDO ES '" + finalCommand + "' Y TARDA " + minValue + " MILISEGUNDOS. ");

            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
            restartVariables();
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void restartVariables() {
        minValue = Float.MAX_VALUE;
        baseValue = 0;
        value = 0;
        super.restartVariables();
    }

    protected float getTimeOnWindows() {
        float result=0;
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
            while(s.hasNext())
            {
                if(s.next().equals("TotalMilliseconds"))
                {
                    s.next();
                    result = Float.parseFloat(s.next().replace(",","."));
                    break;
                }
            }
            s.close();
        }catch (Exception e) {System.out.println("Error calculando el tiempo");}
        return result;
    }

    protected float getTimeOnUnix() {
        float result=0;
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
            result = Float.parseFloat(s.next().replace(",","."));
            s.close();
        }catch (Exception e) {System.out.println("Error calculando el tiempo");}
        return result;
    }
}
