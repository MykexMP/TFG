package model.compiler;

import model.Util;
import model.flag.Flag;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Compiler {
    protected static Compiler instance;

    //GLOBAL
    protected String destiny; // La ruta del fichero compilado
    protected String baseCompileCommand; // El comando de compilación básico, sin flags

    //PHASE 1
    protected  Set<Flag> allFlagsExecuted = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
    protected Set<Flag> flagsProfit = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {}; // Los Flags que se han seleccionado.
    protected List<Flag> flagsNoExecuted = new ArrayList<>(); // Los Flags que no han sido possible ejecutar.
    protected float baseValue = Float.MAX_VALUE; // El valor del comando sin flags.

    //PHASE 2
    protected String combinedFlagsCommand; // Una variable auxiliar para guardar el comando que se va a probar.
    protected List<String> commandsUsed = new ArrayList<>(); // Los Comandos que se han ejecutado previamente.
    protected String finalCommand; // El comando que proporciona el mejor resultado
    protected float minValue = Float.MAX_VALUE; // El valor del mejor comando.
    protected float value = Float.MAX_VALUE; // Un valor auxiliar para comparar con minValue.
    protected Random r = new Random();

    protected final int TIMES_EXECUTION_COMMAND = 3;
    protected final int TIMES_EXECUTION_COMBINATION = 1000;
    protected final int NUMBER_OF_FLAGS = 5;

    protected void generateCommandByRandomSearch(Set<Flag> flagSet) {
        combinedFlagsCommand = baseCompileCommand;
        for (Flag t : flagSet) {
            if (r.nextInt() % 2 == 0) {combinedFlagsCommand = combinedFlagsCommand + " " + t.getFlag(); t.setActive(true);}
            else {combinedFlagsCommand = combinedFlagsCommand + " " + Util.getfnoFlag(t.getFlag()); t.setActive(false);}
        }
    }

    protected void restartVariables() {
        destiny = "";
        baseCompileCommand = "";
        flagsProfit = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
        allFlagsExecuted = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
        flagsNoExecuted = new ArrayList<>();
        baseValue = Float.MAX_VALUE;
        combinedFlagsCommand = "";
        commandsUsed = new ArrayList<>();
        finalCommand = "";
        minValue = Float.MAX_VALUE;
        value = Float.MAX_VALUE;
    }

    protected void generateCommandFromFlags(Set<Flag> flagSet) {
        combinedFlagsCommand = baseCompileCommand;
        for (Flag f: flagSet) {
            if(f.isActive()) combinedFlagsCommand = combinedFlagsCommand + " " + f.getFlag();
            //else combinedFlagsCommand = combinedFlagsCommand + " " + Util.getfnoFlag(f.getFlag()); // Algunos flags -fno fallan y no permiten compilar FIXME
        }
    }

    protected void getFinalCommandByRandomSearch() {
        try {
            for(int i=0;i<TIMES_EXECUTION_COMBINATION;i++) {
                generateCommandByRandomSearch(flagsProfit);
                System.out.println("Número: " + i);
                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand);
                    checkCommand();
                }
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    protected void init(String origin,String libraries) {
        destiny = Util.deleteExtension(origin);
        baseCompileCommand = "g++ -o " + destiny + " " + origin + " " + libraries;
        finalCommand = baseCompileCommand;
        combinedFlagsCommand = baseCompileCommand;
        checkCommand();
    }

    protected void getFinalCommandByHillClimbing(Set<Flag> flagSetSorted) {
        try {
            // Sorted
            // HashSet<Flag> flagSet =  flagSetSorted;

            // Random
            List<Flag> flagList = new ArrayList<>(flagSetSorted);
            Collections.shuffle(flagList);
            HashSet<Flag> flagSet = new HashSet<>(flagList);

            Iterator<Flag> iter = flagSet.iterator();
            Flag selected = null;
            generateCommandByRandomSearch(flagSet);
            checkCommand();
            for(int i=0;i<TIMES_EXECUTION_COMBINATION;i++) {
                System.out.println("Número:"+i);
                if(iter.hasNext()) selected = iter.next();
                else {iter = flagSet.iterator(); selected = iter.next();}
                selected.setActive(!selected.isActive());
                generateCommandFromFlags(flagSet);
                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    checkCommand();
                }else {System.out.println("Comando ya usado");}
                if (!finalCommand.equals(combinedFlagsCommand)) selected.setActive(!selected.isActive());
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    protected void getFinalCommandByMostPromising() {
        Set<Flag> flagSet = new TreeSet<>(Comparator.comparing(Flag::getValue));
        Iterator<Flag> iter = flagsProfit.iterator();
        for (int i=0;i<NUMBER_OF_FLAGS;i++){flagSet.add(iter.next());}

        double limit = Math.pow(2,NUMBER_OF_FLAGS);
        char[] combination;

        for (int j=0;j<limit;j++){
            combination = String.format("%0"+flagSet.size()+"d",Integer.parseInt(Integer.toBinaryString(j))).toCharArray();
            iter = flagSet.iterator();

            System.out.println("Número: " + (j+1));
            System.out.println("Correspondencia en Binario: " +
                    String.format("%0"+flagSet.size()+"d",Integer.parseInt(Integer.toBinaryString(j))));

            for (int k=0;k<combination.length;k++){
                if(combination[k]=='0') iter.next().setActive(false);
                else iter.next().setActive(true);
            }
            generateCommandFromFlags(flagSet);
            checkCommand();
        }
    }

    public abstract void compile(String origin,float threshold,String libraries,List<String> flags);
    protected abstract void getProfitFlags(List<String> flags, float threshold); //FIXME REFACTORIZAR
    protected abstract void checkCommand();
}