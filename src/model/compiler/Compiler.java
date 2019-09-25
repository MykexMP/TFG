package model.compiler;

import model.Util;
import model.algorithm.Algorithm;
import model.flag.Flag;
import java.util.*;

public abstract class Compiler {
    protected static Compiler instance;
    protected static Algorithm algorithm;

    //GLOBAL
    protected String destiny; // La ruta del fichero compilado
    protected String baseCompileCommand; // El comando de compilación básico, sin flags

    //PHASE 1
    protected Set<Flag> allFlagsExecuted = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {}; // Los Flags que se han podido probar.
    protected Set<Flag> flagsProfit = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {}; // Los Flags que se han seleccionado.
    protected List<Flag> flagsNoExecuted = new ArrayList<>(); // Los Flags que no han sido possible ejecutar.
    protected float baseValue = Float.MAX_VALUE; // El valor del comando sin flags.

    //PHASE 2
    protected String finalCommand; // El comando que proporciona el mejor resultado
    protected float minValue = Float.MAX_VALUE; // El valor del mejor comando.
    protected float value = Float.MAX_VALUE; // Un valor auxiliar para comparar con minValue.

    protected final int TIMES_EXECUTION_COMMAND = 3;
    protected final int TIMES_EXECUTION_COMBINATION = 1000;

    /**
     * @param origin Es la ruta del fichero que queremos compilar.
     * @param libraries Es el resto de parametros para ejecutar el comando.
     * @param flags Son los flags que se pueden activar para C/C++.
     * @param compiler Es la clase que declara el cual es el objetivo a minimizar.
     */
    public void compile(String origin, String libraries, List<String> flags, Compiler compiler, Algorithm a) {
        Set<Flag> flagsToUse = allFlagsExecuted;
        algorithm = a;

        init(origin,libraries);
        getProfitFlags(flags);
        Util.randomizeFlagSortedSet(flagsToUse);

        algorithm.init(origin,libraries,compiler);
        algorithm.run(instance,flagsToUse,TIMES_EXECUTION_COMBINATION,baseCompileCommand);

        System.out.println("El mejor comando tarda " + minValue + " y es " + finalCommand );
        restartVariables();
    }

    private void restartVariables() {
        destiny = "";
        baseCompileCommand = "";
        flagsProfit = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
        allFlagsExecuted = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
        flagsNoExecuted = new ArrayList<>();
        baseValue = Float.MAX_VALUE;
        finalCommand = "";
        minValue = Float.MAX_VALUE;
        value = Float.MAX_VALUE;
    }

    /**
     * @param flags Es el conjunto de flags que exploraremos para obtener los que den una mejor eficiencia.
     */
    private void getProfitFlags(List<String> flags) {
        try{
            System.out.println("El comando base tiene una puntuación de " + baseValue);

            for (String flag : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + flag).waitFor()==0) {
                    value = getValueOfFile();

                    System.out.println("El flag '" + flag + "' tiene una puntuación de " + value );

                    allFlagsExecuted.add(new Flag(flag,value));
                    if(value < baseValue) flagsProfit.add(new Flag(flag,value));
                    if(value < minValue ) {minValue = value; finalCommand = baseCompileCommand + " " + flag;}
                    value = Float.MAX_VALUE;
                }
                else flagsNoExecuted.add(new Flag(flag));
            }
        }
        catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }


    private void init(String origin,String libraries) {
        destiny = Util.deleteExtension(origin) + ".exe";
        baseCompileCommand = "g++ -o " + destiny + " " + origin + " " + libraries;
        finalCommand = baseCompileCommand;
        if(getFirstValue()==-1) System.out.println("No es posible compilar tu programa, vuelve a intentarlo.");
    }

    private float getFirstValue() {
        try {
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()==0){
                baseValue = getValueOfFile();
                return 0;
            }else return -1;
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito"); return -1;}
    }

    public abstract float getValueOfFile();
}