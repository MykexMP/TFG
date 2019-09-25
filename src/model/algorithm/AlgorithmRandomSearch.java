package model.algorithm;

import model.compiler.Compiler;
import model.flag.Flag;
import java.util.Set;

public class AlgorithmRandomSearch extends Algorithm{

    public static Algorithm getAlgorithm(){
        if(instance==null || !instance.getClass().getName().equals(AlgorithmRandomSearch.class.getName())) {
            instance = new AlgorithmRandomSearch();
        }
        return instance;
    }

    @Override
    public void run(Compiler compiler, Set<Flag> entrySet, int numberIterations, String baseCompileCommand) {
        try {
            for(int i=0;i<numberIterations;i++) {
                generateCommandByRandomSearch(entrySet);
                System.out.println("Número: " + i);
                if(!commandsUsed.contains(combinedFlagsCommand)){
                    commandsUsed.add(combinedFlagsCommand);
                    System.out.println("El comando a ejecutar es: \n" + combinedFlagsCommand);
                    if(checkCommand()==-1) i--;
                }else {System.out.println("Comando ya usado");i--;}
            }
            if(Runtime.getRuntime().exec(finalCommand).waitFor()==0)
            {
                System.out.println("Ejecutable generado con éxito");
            }
        } catch (Exception e) { System.out.println("No se ha podido ejecutar el comando"); }
    }
}
