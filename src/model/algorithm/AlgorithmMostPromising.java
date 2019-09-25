package model.algorithm;

import model.compiler.Compiler;
import model.flag.Flag;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class AlgorithmMostPromising extends Algorithm {

    private int NUMBER_OF_FLAGS = 10;

    public static Algorithm getAlgorithm(){
        if(instance==null || !instance.getClass().getName().equals(AlgorithmMostPromising.class.getName())) {
            instance = new AlgorithmMostPromising();
        }
        return instance;
    }


    @Override
    public void run(Compiler compiler, Set<Flag> entrySet, int numberIterations, String baseCompileCommand) {
        Set<Flag> flagSet = new TreeSet<>(Comparator.comparing(Flag::getValue));
        Iterator<Flag> iter = entrySet.iterator();
        for (int i=0;i<NUMBER_OF_FLAGS;i++){if(iter.hasNext())flagSet.add(iter.next());}

        if(flagSet.size()>0)
        {
            double limit = Math.min(Math.pow(2,flagSet.size()),numberIterations);
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

                System.out.println("El comando a ejecutar es: \n " + combinedFlagsCommand);

                checkCommand();
            }
        }
    }
}
