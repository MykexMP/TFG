package view;

import model.Util;
import model.algorithm.Algorithm;
import model.algorithm.AlgorithmFactory;
import model.compiler.Compiler;
import model.compiler.CompilerFactory;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CliView {

    private static Algorithm a;
    private static AlgorithmFactory af = new AlgorithmFactory();

    private static Compiler c;
    private static CompilerFactory cf = new CompilerFactory();

    private static List<String> flagsC = Util.getCFlags();
    private static List<String> flagsCPP = Util.getCPPFlags();

    public static void getCliView(String path, String target , String algorithm , String libraries)
    {
            System.out.println("Leyendo el comando...");

            try{
                    if(Util.isFileCompatible(path)) {

                        if (algorithm.equals("-rs")) a = af.getAlgorithm("Random Search");
                        else if (algorithm.equals("-hc")) a = af.getAlgorithm("Hill Climbing");
                        else if (algorithm.equals("-mp")) a = af.getAlgorithm("Most Promising");
                        else {System.out.println("No se reconoce el comando " + algorithm); throw new Exception();}

                        if (target.equals("-time")) c = cf.getCompilator("Tiempo");
                        else if (target.equals("-size")) c = cf.getCompilator("Tamaño");
                        else {System.out.println("No se reconoce el flag " + target); throw new Exception();}

                        if(path.contains(".cpp")) c.compile(path,libraries,flagsCPP,c,a);
                        else c.compile(path,libraries,flagsC,c,a);
                        }
                        else
                        {
                            System.out.println("No es posible compilar este fichero, vuelva a intentarlo.");
                        }
            }
            catch (NumberFormatException e) {System.out.println("El umbral no es un número.");}
            catch (NoSuchElementException e) {System.out.println("No es posible compilar, faltan argumentos.");}
            catch (Exception e) {}
    }
}
