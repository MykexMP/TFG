import view.CliView;
import view.MainView;

public class main {
    public static void main(String[] args) {
        boolean cli = false; //FIXME CAMBIAR POR ARGUMENTOS

        if(cli)
        {
            CliView.getCliView();
        }
        else
        {
            MainView m = new MainView();
            m.setVisible(true);
        }
    }
}