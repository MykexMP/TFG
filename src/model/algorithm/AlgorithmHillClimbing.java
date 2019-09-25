package model.algorithm;

import model.compiler.Compiler;
import model.flag.Flag;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AlgorithmHillClimbing extends Algorithm {

    public static Algorithm getAlgorithm(){
        if(instance==null || !instance.getClass().getName().equals(AlgorithmHillClimbing.class.getName())) {
            instance = new AlgorithmHillClimbing();
        }
        return instance;
    }

    @Override
    public void run(Compiler compiler, Set<Flag> entrySet, int numberIterations, String baseCompileCommand) {
        try {
            HashSet<Flag> flagSet = new HashSet<>(entrySet);

            Iterator<Flag> iter = flagSet.iterator();
            Flag selected = null;
            generateCommandByRandomSearch(flagSet);
            checkCommand();

            for(int i=0;i<numberIterations;i++) {
                if(iter.hasNext()) selected = iter.next();
                else {iter = flagSet.iterator(); selected = iter.next();}
                selected.setActive(!selected.isActive());
                generateCommandFromFlags(flagSet);

                System.out.println("El comando a ejecutar es: \n " + combinedFlagsCommand);

                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    if(checkCommand()==-1) i--;
                }else i--;
                if (!finalCommand.equals(combinedFlagsCommand)) selected.setActive(!selected.isActive());
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0) System.out.println("Ejecutable generado con éxito");
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}
