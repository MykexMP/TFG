package model.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Compiler {
    protected static Compiler instance;

    //GLOBAL
    protected String destiny; // La ruta del fichero compilado
    protected String baseCompileCommand; // El comando de compilación básico, sin flags

    //PHASE 1
    protected List<String> flagsProfit = new ArrayList<>(); // Los Flags que se han seleccionado.
    protected List<String> flagsNoExecuted = new ArrayList<>(); // Los Flags que no han sido possible ejecutar.

    //PHASE 2
    protected String combinedFlagsCommand; // Una variable auxiliar para guardar el comando que se va a probar.
    protected float minValue = Float.MAX_VALUE; // El valor que usaremos para guardar el mejor comando.
    protected List<String> commandsUsed = new ArrayList<>(); // Los Comandos que se han ejecutado previamente.
    protected String finalCommand; // El comando que proporciona el mejor resultado
    protected Random r = new Random();

    protected void generateCommand() {
        combinedFlagsCommand = baseCompileCommand;
        for (String t : flagsProfit) {
            if (r.nextInt() % 2 == 0) {
                combinedFlagsCommand = combinedFlagsCommand + " " + t;
                System.out.println("El flag ' " + t + "' esta en la muestra");
            }
        }
    }

    public abstract void compile(String path,int threshold,List<String> flags);
    protected abstract void getProfitFlags(List<String> flags, int threshold);
    protected abstract void getFinalCommand();
}
