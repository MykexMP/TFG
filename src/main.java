import view.CliView;
import view.MainView;

public class main {
    public static void main(String[] args) {
        boolean cli = (args.length>0&&args[0].equals("-cli"));

        if(cli)
        {
            if(args.length>3)
            {
                String libraries="";
                int i = 4;

                while(i<args.length)
                {
                    libraries = libraries + " " + args[i];
                    i++;
                }

                CliView.getCliView(args[1],args[2],args[3],libraries);
            }
            else System.out.println("No se ha podido leer su comando, vuelva a intentarlo porfavor.");
        }
        else
        {
            MainView m = new MainView();
            m.setVisible(true);
        }
    }
}