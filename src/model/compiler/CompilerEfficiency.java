package model.compiler;

import model.flag.Flag;
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
    public void compile(String origin, float threshold, String libraries, List<String> flags) {
        init(origin,libraries);
        getProfitFlags(flags,threshold);
        getFinalCommandByHillClimbing(allFlagsExecuted); //FIXME A VECES FUNCIONA, RELACCIONADO CON LOS FLAGS, PERO NO ENTIENDO PORQUE.
        //getFinalCommandByRandomSearch(); // FIXME WORKS FINE
        //getFinalCommandByMostPromising(); // FIXME WORKS FINE
        System.out.println("El mejor comando tarda " + minValue + " y es " + finalCommand );
        restartVariables();
    }

    @Override
    protected void getProfitFlags(List<String> flags, float threshold) {
        try{
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()!=0) throw new Exception();
            baseValue = System.getProperty("os.name").startsWith("Windows") ? getTimeOnWindows() : getTimeOnUnix();
            System.out.println("El comando base tarda " + baseValue);

            for (String flag : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + flag).waitFor()==0) {
                    value = System.getProperty("os.name").startsWith("Windows") ? getTimeOnWindows() : getTimeOnUnix();
                    System.out.println("El flag '" + flag + "' hace que tarde " + value + " Milisegundos"); //FIXME DELETE ON PRODUCTION
                    allFlagsExecuted.add(new Flag(flag,value));
                    if(value < baseValue *(1-(threshold/100))) flagsProfit.add(new Flag(flag,value));
                    if(value < minValue ) minValue = value; finalCommand = baseCompileCommand + " " + flag; //FIXME REVISAR
                    value = Float.MAX_VALUE;
                }
                else flagsNoExecuted.add(new Flag(flag));
            }
        }
        catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void restartVariables() {
        super.restartVariables();
    }

    protected float getTimeOnWindows() {
        Scanner s;
        float result;
        float minResult = Float.MAX_VALUE;
        try {
            for(int i=0;i<TIMES_EXECUTION_COMMAND;i++) {
                s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
                while (s.hasNext()) {
                    if (s.next().equals("TotalMilliseconds")) {
                        s.next();
                        result = Float.parseFloat(s.next().replace(",", "."));
                        if (result < minResult) minResult = result;
                        break;
                    }
                }
                s.close();
            }
        }catch (Exception e) {System.out.println("Error calculando el tiempo");}
        return minResult;
    }

    protected float getTimeOnUnix() {
        Scanner s;
        float result;
        float minResult = Float.MAX_VALUE;
        try {
            for(int i=0;i<TIMES_EXECUTION_COMMAND;i++) {
                s = new Scanner(Runtime.getRuntime().exec(executeCommand).getInputStream());
                result = Float.parseFloat(s.next().replace(",", "."));
                if (result < minResult) minResult = result;
                s.close();
            }
        }catch (Exception e) {System.out.println("Error calculando el tiempo");}
        return minResult;
    }

    @Override
    protected void checkCommand() {
        try {
            if(Runtime.getRuntime().exec(combinedFlagsCommand).waitFor()==0){
                value = System.getProperty("os.name").startsWith("Windows") ? getTimeOnWindows() : getTimeOnUnix();
                System.out.println(" y tarda: " + value);
                if(value < minValue) {minValue = value; finalCommand=combinedFlagsCommand;}
            }else {System.out.println("No se ha podio ejecutar el comando con éxito: Waitfor!=0");}
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito");}
    }
}
