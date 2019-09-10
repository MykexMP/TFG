package model.compiler;

import model.Util;
import model.flag.Flag;
import java.util.*;

public abstract class Compiler {
    protected static Compiler instance;

    //GLOBAL
    protected String destiny; // La ruta del fichero compilado
    protected String baseCompileCommand; // El comando de compilación básico, sin flags

    //PHASE 1
    protected Set<Flag> allFlagsExecuted = new TreeSet<Flag>(Comparator.comparing(Flag::getValue)) {};
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
    protected final int NUMBER_OF_FLAGS = 10;
    protected final int TRYS_BEFORE_CHANGE = 5;

    /**
     * @param origin Es la ruta del fichero que queremos compilar.
     * @param threshold Es el umbral (en tanto por uno) de eficiencia requerida para que se acepte un flag.
     * @param libraries Es el resto de parametros para ejecutar el comando.
     * @param flags Son los flags que se pueden activar para C/C++.
     */
    public void compile(String origin, float threshold, String libraries, List<String> flags) {
        Set<Flag> flagsToUse = allFlagsExecuted;

        init(origin,libraries);
        getProfitFlags(flags,threshold);
        Util.randomizeFlagSortedSet(flagsToUse);
        //getFinalCommandByHillClimbing(flagsToUse); // FIXME WORKS FINE, TIEMPO DEMASIADO ALTO, NO ES RENTABLE
        //getFinalCommandByRandomSearch(flagsToUse); // FIXME WORKS FINE
        getFinalCommandByMostPromising(flagsToUse); // FIXME WORKS FINE, BEST TIME BY FAR
        //getFinalCommandByVariableNeigborhoodSearch(flagsProfit);
        System.out.println("El mejor comando tarda " + minValue + " y es " + finalCommand );
        restartVariables();
    }

    /**
     * @param flagSet Es el conjunto de flags que usaremos para generar el comando.
     */
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

    /**
     * @param flagSet Es el conjunto de flags que usaremos para generar el comando.
     */
    protected void generateCommandFromFlags(Set<Flag> flagSet) {
        combinedFlagsCommand = baseCompileCommand;
        for (Flag f: flagSet) {
            if(f.isActive()) combinedFlagsCommand = combinedFlagsCommand + " " + f.getFlag();
            else {combinedFlagsCommand = combinedFlagsCommand + " " + Util.getfnoFlag(f.getFlag());} // Algunos flags -fno fallan y no permiten compilar FIXME
        }
    }

    /**
     * @param flags Es el conjunto de flags que exploraremos para obtener los que den una mejor eficiencia.
     * @param threshold Es el umbral (en tanto por uno) de eficiencia requerida para que se acepte un flag.
     */
    protected void getProfitFlags(List<String> flags, float threshold) {
        try{
            System.out.println("El comando base tarda " + baseValue);

            for (String flag : flags) {
                if(Runtime.getRuntime().exec(baseCompileCommand + " " + flag).waitFor()==0) {
                    value = getValueOfFile();
                    System.out.println("El flag '" + flag + "' hace que tarde " + value + " Milisegundos");
                    allFlagsExecuted.add(new Flag(flag,value));
                    if(value < baseValue *(1-(threshold/100))) flagsProfit.add(new Flag(flag,value));
                    if(value < minValue ) {minValue = value; finalCommand = baseCompileCommand + " " + flag;}
                    value = Float.MAX_VALUE;
                }
                else flagsNoExecuted.add(new Flag(flag));
            }
        }
        catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    /**
     * @param entrySet Es el conjunto de flags que usaremos para generar el comando.
     */
    protected void getFinalCommandByRandomSearch(Set<Flag> entrySet) {
        try {
            for(int i=0;i<TIMES_EXECUTION_COMBINATION;i++) {
                generateCommandByRandomSearch(entrySet);
                System.out.println("Número: " + i);
                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand);
                    if(checkCommand()==-1) i--;
                }else {System.out.println("Comando ya usado");i--;}
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    /**
     * @param origin Es la ruta del fichero que queremos compilar.
     * @param libraries Es el resto de parametros para ejecutar el comando.
     */
    protected void init(String origin,String libraries) {
        destiny = Util.deleteExtension(origin) + ".exe";
        baseCompileCommand = "g++ -o " + destiny + " " + origin + " " + libraries;
        finalCommand = baseCompileCommand;
        combinedFlagsCommand = baseCompileCommand;
        getFirstValue();
    }

    protected void getFinalCommandByHillClimbing(Set<Flag> flagSetSorted) {
        try {
            HashSet<Flag> flagSet = new HashSet<>(flagSetSorted);

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
                    if(checkCommand()==-1) i--;
                }else {System.out.println("Comando ya usado");i--;}
                if (!finalCommand.equals(combinedFlagsCommand)) selected.setActive(!selected.isActive());
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    protected void getFinalCommandByVariableNeigborhoodSearch(Set<Flag> entryFlagSet) { //FIXME ABORTAR ESTE METODO
        try {
            HashSet<Flag> flagSet = new HashSet<>(entryFlagSet);
            float counter=0,numberFlags=1,numberFlagsSelected=0;

            Iterator<Flag> iter = flagSet.iterator();
            List<Flag> selected = new ArrayList<>();
            generateCommandByRandomSearch(flagSet);
            checkCommand();

            for(int i=0;i<TIMES_EXECUTION_COMBINATION;i++) {
                System.out.println("Número:"+i);

                if(counter>=TRYS_BEFORE_CHANGE){
                    numberFlags++;
                    counter=0;
                }

                //FIXME ¿SELECCIONAR FLAGS EN ORDEN ALEATORIO, NO UNO Y SUS CONSECUTIVOS?
                while(numberFlags>numberFlagsSelected)
                {
                    if(iter.hasNext()) {selected.add(iter.next());numberFlagsSelected++;}
                    else {iter = flagSet.iterator();selected.add(iter.next());numberFlagsSelected++;}
                }

                System.out.println("Number of Flags: " + numberFlags);
                System.out.println("Number of Flags Selected: " + numberFlagsSelected);
                System.out.println("Iterations done on this step: " + counter);

                for (Flag f : selected) {f.setActive(!f.isActive());
                    System.out.println(f.getFlag() + " is " + f.isActive());}

                generateCommandFromFlags(flagSet);
                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    if(checkCommand()==-1) i--;
                }else {System.out.println("Comando ya usado");i--;}

                if(!finalCommand.equals(combinedFlagsCommand)) {
                    counter++;
                    for (Flag f : selected) {f.setActive(!f.isActive());}
                }

                numberFlagsSelected=0;
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }

    protected void getFinalCommandByMostPromising(Set<Flag> entryFlagSet) {
        Set<Flag> flagSet = new TreeSet<>(Comparator.comparing(Flag::getValue));
        Iterator<Flag> iter = entryFlagSet.iterator();
        for (int i=0;i<NUMBER_OF_FLAGS;i++){if(iter.hasNext())flagSet.add(iter.next());}

        if(flagSet.size()>0)
        {
            double limit = Math.min(Math.pow(2,flagSet.size()),TIMES_EXECUTION_COMBINATION);
            char[] combination;

            for (int j=0;j<limit;j++){
                combination = String.format("%0"+flagSet.size()+"d",Integer.parseInt(Integer.toBinaryString(j))).toCharArray();
                iter = flagSet.iterator();

                System.out.println("Número: " + (j));
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
    }

    protected float getFirstValue() {
        try {
            if(Runtime.getRuntime().exec(baseCompileCommand).waitFor()==0){
                baseValue = getValueOfFile();
                return 0;
            }else {System.out.println("No se ha podio ejecutar el comando con éxito: Waitfor!=0"); return -1;}
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito"); return -1;}
    }

    protected int checkCommand() {
        try {
            if(Runtime.getRuntime().exec(combinedFlagsCommand).waitFor()==0){
                value = getValueOfFile();
                System.out.println(" y tarda: " + value);
                if(value < minValue) {minValue = value; finalCommand=combinedFlagsCommand;}
                return 0;
            }else {System.out.println("No se ha podio ejecutar el comando con éxito: Waitfor!=0"); return -1;}
        } catch (Exception e) {System.out.println("No se ha podio ejecutar el comando con éxito"); return -1;}
    }

    protected abstract float getValueOfFile();
}