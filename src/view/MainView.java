package view;
import model.Compiler;
import model.CompilerEficiency;
import model.CompilerFactory;
import model.CompilerSize;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.List;

public class MainView extends JFrame {
    private JComboBox priority;
    private JComboBox threshold;
    private JLabel labelPriority;
    private JLabel labelThreshold;
    private JPanel rootPanel;
    private JLabel welcomeLabel;
    private JButton compileButton;
    private JProgressBar compilationProgressBar;
    private JTextField pathFile;

    private Compiler c = CompilerEficiency.getCompiler();
    private CompilerFactory cf = new CompilerFactory();

    public MainView()
    {
        initWindow();

        compileButton.addActionListener(i -> c.compile(pathFile.getText(),Integer.parseInt((String )threshold.getSelectedItem())));

        priority.addItemListener(e -> {
            if(e.getStateChange()== ItemEvent.SELECTED){
                c = cf.crearCompilador((String)e.getItem());
                for (ActionListener al : compileButton.getActionListeners()) {
                    compileButton.removeActionListener(al);
                }

                compileButton.addActionListener(i -> c.compile(pathFile.getText(),Integer.parseInt((String )threshold.getSelectedItem())));
            }
        });

        pathFile.setDropTarget(new DropTarget() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f: files ){pathFile.setText(f.getAbsolutePath());}

                    compilationProgressBar.setVisible(true);
                    compileButton.setVisible(true);
                } catch (Exception e) {}
            }
        });
    }

    private void initWindow()
    {
        add(rootPanel);

        compilationProgressBar.setVisible(false);
        compileButton.setVisible(false);

        setTitle("Compilador Inteligente");
        setSize(500,400);
    }
}
