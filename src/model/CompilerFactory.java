package model;

public class CompilerFactory {
    public final static String EFICIENCIA = "Eficiencia";
    public final static String TAMAÑO = "Tamaño";

    public Compiler crearCompilador(String tipo)
    {
        switch (tipo)
        {
            case EFICIENCIA: return CompilerEficiency.getCompiler();
            case TAMAÑO: return CompilerSize.getCompiler();
            default: return null;
        }
    }
}
