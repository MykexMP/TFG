package view;

import model.Util;
import model.compiler.Compiler;
import model.compiler.CompilerFactory;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CliView {

    private static Compiler c;
    private static CompilerFactory cf = new CompilerFactory();

    private static List<String> flagsC = Util.getCFlags();
    private static List<String> flagsCPP = Util.getCPPFlags();

    public static void getCliView()
    {
            System.out.println("Bienvenido al Sistema de Compilación Inteligente, porfavor introduzca el comando de compilación siguiendo las directrices. ");
            System.out.println("- Las rutas de los ficheros deben ser absolutas.");
            System.out.println("- No debe de haber espacios en dichas rutas.");
            System.out.println("Por último añade argumentos para el objetivo (-efi,-size) , el umbral (-[number 0-100]) y librerias si fuera necesario.");
            System.out.println("Por ejemplo: g++ -o C:\\Users\\Mykex\\Desktop\\main C:\\Users\\Mykex\\Desktop\\main.c -efi -20 -pthread");
            System.out.println("Es importante que rellene cada campo.");

        while(true)
        {

            Scanner sc = new Scanner(System.in);
            String command = sc.nextLine();
            StringTokenizer st = new StringTokenizer(command," ");

            System.out.println("Leyendo el comando...");

            try{

                if(st.nextToken().equals("g++")&&st.nextToken().equals("-o")&&st.nextToken()!=null)
                {
                    String path = st.nextToken();

                    if(Util.isFileCompatible(path)) {

                        String target = st.nextToken();
                        String threshold = st.nextToken();
                        String libraries = st.nextToken("");

                        float numberThreshold = Float.parseFloat(threshold.substring(1));

                        if (target.equals("-efi")) c = cf.getCompilator("Eficiencia");
                        else if (target.equals("-size")) c = cf.getCompilator("Tamaño");
                        else {System.out.println("No se reconoce el flag " + target); throw new Exception();}

                        if(path.contains(".cpp")) c.compile(path,numberThreshold,libraries,flagsCPP);
                        else c.compile(path,numberThreshold,libraries,flagsC);
                        }
                        else
                        {
                            System.out.println("No es posible compilar este fichero, vuelva a intentarlo.");
                        }
                }
                else
                {
                    System.out.println("No se ha podido procesar su comando, vuelva a intentarlo.");
                }
            }
            catch (NumberFormatException e) {System.out.println("El umbral no es un número.");}
            catch (NoSuchElementException e) {System.out.println("No es posible compilar, faltan argumentos.");}
            catch (Exception e) {}
        }
    }
}
