package model.compiler;

public class CompilerFactory {
    public final static String EFICIENCIA = "Eficiencia";
    public final static String TAMAÑO = "Tamaño";

    public Compiler getCompilator(String tipo)
    {
        switch (tipo)
        {
            case EFICIENCIA: return CompilerEfficiency.getCompiler();
            case TAMAÑO: return CompilerSize.getCompiler();
            default: return null;
        }
    }
}
