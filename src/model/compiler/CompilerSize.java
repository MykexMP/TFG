package model.compiler;

import java.io.File;

public class CompilerSize extends Compiler {

    public static Compiler getCompiler(){
        if(instance==null || !instance.getClass().getName().equals(CompilerSize.class.getName())) {
            instance = new CompilerSize();
        }
            return instance;
    }

    /**
     *
     * @return Devuelve el tamaño del fichero.
     */
    @Override
    public float getValueOfFile(){return new File(destiny).length();}
}