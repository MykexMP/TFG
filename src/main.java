import view.CliView;
import view.MainView;

public class main {
    public static void main(String[] args) {
        //boolean cli = args[0].equals("cli");
        boolean cli = false;

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