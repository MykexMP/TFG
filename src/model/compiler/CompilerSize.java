package model.compiler;

import model.flag.Flag;

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
    public void compile(String origin, float threshold,String libraries, List<String> flags) {
        init(origin,libraries);
        getProfitFlags(flags,threshold);
        getFinalCommandByRandomSearch();
        restartVariables();
    }

    @Override
    protected void getProfitFlags(List<String> flags, float threshold){
        try{
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()!=0) throw new Exception();
            baseValue = new File(destiny).length();

            for (String s : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + s).waitFor()==0) {
                    value = new File(destiny).length();
                    System.out.println("El flag '" + s + "' le da tamaño " + value + " Bytes" ); //FIXME DELETE THIS LINE ON PRODUCTION
                    if (baseValue *(1-threshold) > value) flagsProfit.add(new Flag(s,value));
                }
                else flagsNoExecuted.add(new Flag(s));
            }
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    @Override
    protected void restartVariables() {
        super.restartVariables();
    }

    protected void checkCommand() {
        try {
            if (Runtime.getRuntime().exec(combinedFlagsCommand).waitFor() == 0) {
                value = new File(destiny).length();
                System.out.println(" y pesa " + value + " Bytes"); //FIXME DELETE THIS LINE ON PRODUCTION
                if (value < minValue) {minValue = value; finalCommand=combinedFlagsCommand;}
            }
        } catch (Exception e) {e.printStackTrace();}
    }
}