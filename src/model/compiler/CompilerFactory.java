package model.compiler;

public class CompilerFactory {
    private final static String TIEMPO = "Tiempo";
    private final static String TAMAÑO = "Tamaño";

    public Compiler getCompilator(String tipo)
    {
        switch (tipo)
        {
            case TIEMPO: return CompilerTime.getCompiler();
            case TAMAÑO: return CompilerSize.getCompiler();
            default: return null;
        }
    }
}
