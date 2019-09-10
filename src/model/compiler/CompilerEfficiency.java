package model.compiler;

import java.util.Scanner;

public class CompilerEfficiency extends Compiler{
    private String executeCommand;

    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerEfficiency.class.getName())){
            instance = new CompilerEfficiency();
            return instance;
        }else return instance;
    }

    @Override
    protected float getValueOfFile(){
        executeCommand = System.getProperty("os.name").startsWith("Windows") ? "powershell.exe Measure-Command{" + destiny + "}" : "/usr/bin/time -f \"%e\" " + destiny;
        //Segundos -e
        return System.getProperty("os.name").startsWith("Windows") ? getTimeOnWindows() : getTimeOnUnix();
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
                String aux = "";
                while(s.hasNext()) aux = s.next();
                result = Float.parseFloat(aux.replace(",", "."));
                result *= 1000;
                if (result < minResult) minResult = result;
                s.close();
            }
        }catch (Exception e) {System.out.println("Error calculando el tiempo");}
        return minResult;
    }
}
