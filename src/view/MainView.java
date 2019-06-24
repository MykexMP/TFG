package view;

import model.Util;
import model.compiler.Compiler;
import model.compiler.CompilerEfficiency;
import model.compiler.CompilerFactory;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import static model.Util.*;

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
    private JButton fileExplorer;
    private JTextField libraries;

    private Compiler c = CompilerEfficiency.getCompiler();
    private CompilerFactory cf = new CompilerFactory();

    private List<String> flagsC = Util.getCFlags();
    private List<String> flagsCPP = Util.getCPPFlags();

    public MainView()
    {
        initWindow();

        libraries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                libraries.setText("");
            }
        });

        fileExplorer.addActionListener(i -> {
            fileExplorer();
            refreshCompileButton();
        });

        priority.addItemListener(e -> {
            if(e.getStateChange()== ItemEvent.SELECTED){
                c = cf.getCompilator((String)e.getItem());
                refreshCompileButton();
            }
        });

        pathFile.setDropTarget(new DropTarget() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f: files ){
                        checkFile(f);
                    }
                    refreshCompileButton();
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

    private void refreshCompileButton() {
        for (ActionListener al : compileButton.getActionListeners()) {
            compileButton.removeActionListener(al);
        }

        if(pathFile.getText().charAt(pathFile.getText().length()-1)=='c'){
            compileButton.addActionListener(i -> c.compile(pathFile.getText(),Float.parseFloat((String)threshold.getSelectedItem()),libraries.getText(),flagsC));
        }else{
            compileButton.addActionListener(i -> c.compile(pathFile.getText(),Float.parseFloat((String)threshold.getSelectedItem()),libraries.getText(),flagsCPP));
        }
    }

    private void fileExplorer()
    {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Ficheros C y C++", "c", "cpp");
        fc.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        fc.setFileFilter(filter);
        fc.showOpenDialog(this);

        File f = fc.getSelectedFile();
        if (f!=null) checkFile(f);
    }

    private void checkFile(File f)
    {
        if(isFileCompatible(f.getAbsolutePath())) {
            pathFile.setText(f.getAbsolutePath());
            compilationProgressBar.setVisible(true);
            compileButton.setVisible(true);
            pathFile.setEditable(false);
        }
        else {
            System.out.println("El archivo no es un fichero c o c++"); // Sustituir por excepción
            pathFile.setText("Suelta aquí el fichero a compilar...");
            compilationProgressBar.setVisible(false);
            compileButton.setVisible(false);
            pathFile.setEditable(true);
        }
    }
}
