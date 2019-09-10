package model.compiler;

import java.io.File;

public class CompilerSize extends Compiler {

    private static final long DELAY = 5000;

    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerSize.class.getName())){
            instance = new CompilerSize();
            return instance;
        }else return instance;
    }

    @Override
    protected float getValueOfFile(){return new File(destiny).length();}
}