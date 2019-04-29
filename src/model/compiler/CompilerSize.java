package model.compiler;

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
    public void compile(String origin, int threshold, List<String> flags) {
        init(origin);
        getProfitFlags(flags,threshold);
        getFinalCommand();
        restartVariables();
    }

    @Override
    protected void getProfitFlags(List<String> flags, int threshold){
        try{
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()!=0) throw new Exception();
            baseValue = new File(destiny).length();

            for (String s : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + s).waitFor()==0) {
                    value = new File(destiny).length();

                    System.out.println("El flag '" + s + "' le da tamaño " + value + " Bytes" ); //FIXME DELETE THIS LINE ON PRODUCTION

                    if (baseValue *(1-threshold) > value) flagsProfit.add(s);
                }
                else flagsNoExecuted.add(s);
            }
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void getFinalCommand() {
        try {
            for(int i=0;i<1000;i++) {
                generateCommandByRandomSearch();

                if(!commandsUsed.contains(combinedFlagsCommand)) {
                    commandsUsed.add(combinedFlagsCommand);

                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand); //FIXME DELETE THIS LINE ON PRODUCTION

                    if (Runtime.getRuntime().exec(combinedFlagsCommand).waitFor() == 0) {
                        value = new File(destiny).length();
                        System.out.println(" y pesa " + value + " Bytes"); //FIXME DELETE THIS LINE ON PRODUCTION
                        if (value < minValue) {
                            minValue = value; finalCommand=combinedFlagsCommand;}
                    }
                }
            }

            System.out.println("EL MEJOR COMANDO ES '" + finalCommand + "' Y PESA " + minValue + " BYTES. "); //FIXME DELETE THIS LINE ON PRODUCTION

            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
            restartVariables();
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void restartVariables() {
        minValue = Long.MAX_VALUE;
        baseValue = 0;
        value = 0;
        super.restartVariables();
    }
}