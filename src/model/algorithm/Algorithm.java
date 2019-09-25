package model.algorithm;

import model.Util;
import model.compiler.Compiler;
import model.flag.Flag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Algorithm {
    protected static Algorithm instance;
    protected static Compiler compiler;

    protected String combinedFlagsCommand;
    protected String finalCommand;
    protected String baseCompileCommand;
    protected String destiny;

    protected float value;
    protected float minValue;
    protected float baseValue;

    protected List<String> commandsUsed = new ArrayList<>();

    protected int checkCommand() {
        try {
            if(Runtime.getRuntime().exec(combinedFlagsCommand).waitFor()==0){
                value = compiler.getValueOfFile();
                System.out.println(" y tarda: " + value);
                if(value < minValue) {minValue = value; finalCommand=combinedFlagsCommand;}
                return 0;
            }else return -1;
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito"); return -1;}
    }

    /**
     * @param flagSet Es el conjunto de flags que usaremos para generar el comando.
     */
    protected void generateCommandByRandomSearch(Set<Flag> flagSet) {
        Random r = new Random();
        combinedFlagsCommand = baseCompileCommand;
        for (Flag t : flagSet) {
            if (r.nextInt() % 2 == 0) {combinedFlagsCommand = combinedFlagsCommand + " " + t.getFlag(); t.setActive(true);}
            else {combinedFlagsCommand = combinedFlagsCommand + " " + Util.getfnoFlag(t.getFlag()); t.setActive(false);}
        }
    }

    /**
     * @param origin Es la ruta del fichero que queremos compilar.
     * @param libraries Es el resto de parametros para ejecutar el comando.
     * @param compiler Es la clase que declara el cual es el objetivo a minimizar.
     */
    public void init(String origin,String libraries, Compiler compiler) {
        this.compiler = compiler;
        destiny = Util.deleteExtension(origin) + ".exe";
        baseCompileCommand = "g++ -o " + destiny + " " + origin + " " + libraries;
        finalCommand = baseCompileCommand;
        combinedFlagsCommand = baseCompileCommand;
        getFirstValue();
    }

    protected float getFirstValue() {
        try {
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()==0){
                baseValue = compiler.getValueOfFile();
                return 0;
            }else return -1;
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito"); return -1;}
    }


    /**
     * @param flagSet Es el conjunto de flags que usaremos para generar el comando.
     */
    protected void generateCommandFromFlags(Set<Flag> flagSet) {
        combinedFlagsCommand = baseCompileCommand;
        for (Flag f: flagSet) {
            if(f.isActive()) combinedFlagsCommand = combinedFlagsCommand + " " + f.getFlag();
            else {combinedFlagsCommand = combinedFlagsCommand + " " + Util.getfnoFlag(f.getFlag());}
        }
    }

    /**
     *
     * @param compiler El compilador que da el objetivo de compilación.
     * @param entrySet El conjunto de flags de entrada, los que mejoran.
     * @param numberIterations El número de iteraciones, dado por el usuario.
     * @param baseCompileCommand El comando base de compilación.
     */
    public abstract void run(Compiler compiler, Set<Flag> entrySet, int numberIterations, String baseCompileCommand);
}
